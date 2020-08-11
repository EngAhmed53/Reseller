package com.shouman.apps.reseller.admin.ui.auth.completeUserInfo

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.shouman.apps.reseller.admin.api.ResponseCode
import com.shouman.apps.reseller.admin.api.UsersApi
import com.shouman.apps.reseller.admin.api.UsersApiServices
import com.shouman.apps.reseller.admin.data.model.Company
import com.shouman.apps.reseller.admin.preferences.UserPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*


enum class UploadInfoStatus {
    COMPLETE,
    START_PHOTO_UPLOAD,
    START_COMPANY_INFO_UPLOAD,
    UPLOAD_LOGO_ERROR,
    FIREBASE_UID_ERROR,
    ERROR,
    CONNECTION_TIME_OUT
}


class CompleteUserInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val TIME_MILLISECOND: Long = 20000

    private var selectedLogo: Bitmap? = null
    private var fullOwnerNumber: String? = null

    private val context = application

    lateinit var mCapturedPhotoLocalPath: String

    private lateinit var usersApiServices: UsersApiServices

    private val job = Job()

    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)

    private val _openSelectPhotoBottomSheet = MutableLiveData<Boolean?>()
    val openSelectPhotoBottomSheet: LiveData<Boolean?>
        get() = _openSelectPhotoBottomSheet

    private val _uploadCompanyInfoStatus = MutableLiveData<UploadInfoStatus?>()
    val uploadCompanyInfoStatus: LiveData<UploadInfoStatus?>
        get() = _uploadCompanyInfoStatus


    private val _getPhotoUsingIntent = MutableLiveData<Intent?>()
    val getPhotoUsingIntent: LiveData<Intent?>
        get() = _getPhotoUsingIntent

    private val _isPhotoSelected = MutableLiveData<Boolean?>()
    val isPhotoSelected: LiveData<Boolean?>
        get() = _isPhotoSelected

    private val _deleteSelectedPhoto = MutableLiveData<Boolean?>()
    val deleteSelectedPhoto: LiveData<Boolean?>
        get() = _deleteSelectedPhoto


    fun selectPhoto() {
        if (_isPhotoSelected.value == true) {
            _deleteSelectedPhoto.value = true
            _isPhotoSelected.value = false
        } else {
            _openSelectPhotoBottomSheet.value = true
        }
    }

    /**
     * open the camera using intent to capture a image.
     */
    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (e: IOException) {
            null
        }
        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                context,
                "com.shouman.apps.reseller.admin.fileprovider",
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            _getPhotoUsingIntent.value = takePictureIntent
        }
    }

    /**
     * create a file to for the image captured from the camera.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat.getDateInstance().format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        mCapturedPhotoLocalPath = image.absolutePath
        return image
    }

    /**
     * open the file chooser using intent to choose an image
     */
    fun openFiles() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        _getPhotoUsingIntent.value = chooserIntent
    }


    /**
     * convert the bitmap the user choose to byteArray to upload it to firebase storage.
     */
    private fun convertBitmapToByteArray(): ByteArray? {
        selectedLogo?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.WEBP, 25, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
        return null
    }

    /**
     *upload the user selected photo to firebase storage under his uid name
     * photo path = firebaseUID/logo.webp
     * if there is a selected logo but the upload has an exception,
     * we stop the whole upload job and cancel it and inform the user with the error
     */
    private suspend fun uploadPhotoToFirebaseStorage(): String? {
        convertBitmapToByteArray()?.let {
            val firebaseStorage = Firebase.storage
            val logoReference =
                firebaseStorage.getReference("${FirebaseAuth.getInstance().currentUser?.uid}/logo.webp")

            return try {

                //upload photo
                logoReference.putBytes(it).await()

                //get photo path
                logoReference.downloadUrl.await()?.toString()

            } catch (e: FirebaseNetworkException) {

                Log.e("connection error", "error", e)

                //inform the user that is an error while uploading the photo
                _uploadCompanyInfoStatus.postValue(UploadInfoStatus.CONNECTION_TIME_OUT)

                // return null
                null
            } catch (e: Exception) {
                Log.e("upload photo error", "error", e)

                //inform the user that is an error while uploading the photo
                _uploadCompanyInfoStatus.postValue(UploadInfoStatus.UPLOAD_LOGO_ERROR)

                // return null
                null
            }
        }

        // there is no photo selected the photo path well be null
        return null
    }

    /**
     * set the company object to send ot to server
     */
    private suspend fun setCompanyInfo(ownerName: String, companyName: String): Company {
        _uploadCompanyInfoStatus.postValue(UploadInfoStatus.START_PHOTO_UPLOAD)
        val logoPhotoPath = uploadPhotoToFirebaseStorage()
        return Company(-555, ownerName, fullOwnerNumber!!, companyName, logoPhotoPath)
    }


    /**
     *upload the new company info to the server
     * if the server response code is success, update the userPreferences with the new userObject from the server
     * else we inform the user with the error
     */
    fun uploadCompanyInfoToServer(ownerName: String, companyName: String) {
        coroutineScope.launch {
            val uploadJob = withTimeoutOrNull(TIME_MILLISECOND) {
                try {

                    startUpload(ownerName, companyName)

                } catch (e: SocketTimeoutException) {
                    Log.e("error", "error", e)

                    //inform the user that is an error while uploading info
                    _uploadCompanyInfoStatus.postValue(UploadInfoStatus.CONNECTION_TIME_OUT)
                } catch (e: Exception) {
                    Log.e("error", "error", e)

                    //inform the user that is an error while uploading info
                    _uploadCompanyInfoStatus.postValue(UploadInfoStatus.ERROR)
                }
            }
            if (uploadJob == null) _uploadCompanyInfoStatus.postValue(UploadInfoStatus.CONNECTION_TIME_OUT)
        }
    }

    private suspend fun startUpload(ownerName: String, companyName: String) {

        val company = setCompanyInfo(ownerName, companyName)

        _uploadCompanyInfoStatus.postValue(UploadInfoStatus.START_COMPANY_INFO_UPLOAD)
        usersApiServices = UsersApi.usersService

        val serverResponse = usersApiServices.updateCompanyInfoAsync(
            UserPreferences.getFirebaseUID(context)!!,
            company
        ).await()

        when (serverResponse.responseCode) {
            ResponseCode.SUCCESS -> {
                UserPreferences.setUserInfo(context, serverResponse.body)
                _uploadCompanyInfoStatus.postValue(UploadInfoStatus.COMPLETE)
            }
            ResponseCode.FIREBASE_CODE_NOT_VALID -> _uploadCompanyInfoStatus.postValue(
                UploadInfoStatus.FIREBASE_UID_ERROR
            )
            else -> _uploadCompanyInfoStatus.postValue(UploadInfoStatus.ERROR)
        }
    }

    fun setCompletePhoneNumber(fullNumber: String) {
        this.fullOwnerNumber = fullNumber
    }

    fun setBitmap(bitmap: Bitmap) {
        this.selectedLogo = bitmap
    }

    fun logoImageSettingDone() {
        _isPhotoSelected.value = true
    }

    fun doneOpenBottomSheet() {
        _openSelectPhotoBottomSheet.value = null
    }

    fun doneDeletingPhoto() {
        _deleteSelectedPhoto.value = null
    }

    fun doneOpenIntent() {
        _getPhotoUsingIntent.value = null
    }

    fun restoreUploadStatus() {
        _uploadCompanyInfoStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}