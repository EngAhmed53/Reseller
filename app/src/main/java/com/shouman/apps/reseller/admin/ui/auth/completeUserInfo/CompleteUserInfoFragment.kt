package com.shouman.apps.reseller.admin.ui.auth.completeUserInfo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.FragmentCompleteUserInfoBinding
import com.shouman.apps.reseller.admin.ui.auth.AuthActivity
import com.shouman.apps.reseller.admin.ui.auth.AuthViewModel
import com.shouman.apps.reseller.admin.ui.auth.Path
import com.shouman.apps.reseller.admin.utils.BitmapUtils


class CompleteUserInfoFragment : Fragment() {


    private lateinit var mBinding: FragmentCompleteUserInfoBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userInfoViewModel: CompleteUserInfoViewModel
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_CHOOSER = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentCompleteUserInfoBinding.inflate(inflater)

        //register ccp with phone number edit text
        mBinding.ccp.registerPhoneNumberTextView(mBinding.phoneNumEditText)


        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        userInfoViewModel =
            ViewModelProvider(requireActivity() as AuthActivity).get(CompleteUserInfoViewModel::class.java)

        mBinding.lifecycleOwner = this
        mBinding.userInfoViewModel = userInfoViewModel

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.path.observe(viewLifecycleOwner, Observer { path ->
            path?.let {
                when (it) {
                    Path.FIREBASE_USER_IS_NOT_NULL_AND_PREFERENCE_SETTED -> navigateToMain()
                    else -> return@let
                }
            }
        })


        userInfoViewModel.openSelectPhotoBottomSheet.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val toSelectPhoto =
                    CompleteUserInfoFragmentDirections.actionCompleteUserInfoFragmentToSelectPhotoBottomSheet()
                findNavController().navigate(toSelectPhoto)
                userInfoViewModel.doneOpenBottomSheet()
            }
        })

        userInfoViewModel.getPhotoUsingIntent.observe(viewLifecycleOwner, Observer { intent ->
            intent?.let {
                when (intent.action) {
                    MediaStore.ACTION_IMAGE_CAPTURE -> intent.also { cameraIntent ->
                        cameraIntent.resolveActivity(requireActivity().packageManager)?.also {
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
                            userInfoViewModel.doneOpenIntent()
                        }
                    }
                    Intent.ACTION_CHOOSER -> intent.also { imageChooser ->
                        imageChooser.resolveActivity(requireActivity().packageManager)?.also {
                            startActivityForResult(imageChooser, REQUEST_IMAGE_CHOOSER)
                            userInfoViewModel.doneOpenIntent()
                        }
                    }
                    else -> return@let
                }
            }
        })

        userInfoViewModel.isPhotoSelected.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                mBinding.selectRemoveButton.setImageResource(R.drawable.ic_close)
            } else {
                mBinding.selectRemoveButton.setImageResource(R.drawable.ic_camera_alt_24)
            }
        })

        userInfoViewModel.deleteSelectedPhoto.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                mBinding.profileImg.setImageResource(android.R.color.transparent)
                userInfoViewModel.doneDeletingPhoto()
            }
        })

        userInfoViewModel.deleteSelectedPhoto.observe(viewLifecycleOwner, Observer {
            if (it == true) {

                mBinding.profileImg.setImageResource(android.R.color.transparent)

                // Otherwise, delete the temporary image file
                BitmapUtils.deleteImageFile(
                    requireContext(),
                    userInfoViewModel.mCapturedPhotoLocalPath
                )
                userInfoViewModel.doneDeletingPhoto()
            }
        })

        userInfoViewModel.uploadCompanyInfoStatus.observe(
            viewLifecycleOwner,
            Observer { uploadStatus ->
                when (uploadStatus) {
                    UploadInfoStatus.START_PHOTO_UPLOAD,
                    UploadInfoStatus.START_COMPANY_INFO_UPLOAD -> return@Observer

                    UploadInfoStatus.CONNECTION_TIME_OUT -> {
                        val contextView = mBinding.profileImg
                        Snackbar.make(
                            contextView, R.string.connection_time_out, Snackbar.LENGTH_SHORT
                        )
                            .show()
                        userInfoViewModel.restoreUploadStatus()
                    }

                    UploadInfoStatus.COMPLETE -> {
                        authViewModel.setNavPath()
                        userInfoViewModel.restoreUploadStatus()
                    }

                    UploadInfoStatus.ERROR -> {
                        val contextView = mBinding.profileImg
                        Snackbar.make(contextView, R.string.upload_error, Snackbar.LENGTH_LONG)
                            .show()
                        userInfoViewModel.restoreUploadStatus()
                    }

                    UploadInfoStatus.FIREBASE_UID_ERROR -> {
                        val contextView = mBinding.profileImg
                        Snackbar.make(contextView, R.string.firebase_uid_error, Snackbar.LENGTH_LONG)
                            .show()
                        userInfoViewModel.restoreUploadStatus()
                    }

                    UploadInfoStatus.UPLOAD_LOGO_ERROR -> {
                        val contextView = mBinding.profileImg
                        Snackbar.make(contextView, R.string.upload_logo_error, Snackbar.LENGTH_LONG)
                            .show()
                        userInfoViewModel.restoreUploadStatus()
                    }
                }
            })
    }

    private fun navigateToMain() {
        val toMain =
            CompleteUserInfoFragmentDirections.actionCompleteUserInfoFragmentToMainActivity()
        findNavController().navigate(toMain)
        requireActivity().finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            //image coming from the camera
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    // Resample the saved image to fit the ImageView
                    BitmapUtils.resamplePic(
                        mBinding.profileImg.width,
                        mBinding.profileImg.height,
                        userInfoViewModel.mCapturedPhotoLocalPath
                    )?.let {
                        mBinding.profileImg.setImageBitmap(it)
                        userInfoViewModel.logoImageSettingDone()
                        userInfoViewModel.setBitmap(it)
                    }
                } else {
                    // Otherwise, delete the temporary image file
                    BitmapUtils.deleteImageFile(
                        requireContext(),
                        userInfoViewModel.mCapturedPhotoLocalPath
                    )
                }
            }

            //image coming from the file chooser or gallery
            REQUEST_IMAGE_CHOOSER -> {
                if (data == null || data.data == null) return
                val bitmap: Bitmap? = if (android.os.Build.VERSION.SDK_INT >= 29) {
                    // To handle deprication use
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            requireContext().contentResolver,
                            data.data!!
                        )
                    )
                } else {
                    // Use older version
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        data.data!!
                    )
                }
                bitmap?.let {
                    mBinding.profileImg.setImageBitmap(it)
                    userInfoViewModel.logoImageSettingDone()
                    userInfoViewModel.setBitmap(it)
                }
            }
        }

//            val bitmap: Bitmap? = if (android.os.Build.VERSION.SDK_INT >= 29) {
//                // To handle deprication use
//                ImageDecoder.decodeBitmap(
//                    ImageDecoder.createSource(
//                        requireContext().contentResolver,
//                        Uri.fromFile(file)
//                    )
//                )
//            } else {
//            // Use older version
//                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.fromFile(file))
//            }
//
//            bitmap?.let {
//                mBinding.profileImg.setImageBitmap(bitmap)
//            }
    }
}
