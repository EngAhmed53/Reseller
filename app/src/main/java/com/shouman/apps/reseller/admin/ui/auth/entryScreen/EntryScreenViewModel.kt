package com.shouman.apps.reseller.admin.ui.auth.entryScreen

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.api.*
import com.shouman.apps.reseller.admin.preferences.UserPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.net.SocketTimeoutException


enum class SignInStatus {
    STARTED,
    COMPLETE,
    CREDENTIAL_ERROR,
    FIREBASE_UID_ERROR,
    USER_COLLISION_ERROR,
    ERROR,
    CONNECTION_TIME_OUT
}


class EntryScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val TIME_MILLISECOND: Long = 10000
    private val context = application

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var callbackManager: CallbackManager

    private lateinit var usersApiServices: UsersApiServices

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val _googleSignInIntent = MutableLiveData<Intent?>()
    val googleSignInIntent: LiveData<Intent?>
        get() = _googleSignInIntent

    private val _facebookCallback = MutableLiveData<CallbackManager?>()
    val facebookCallback: LiveData<CallbackManager?>
        get() = _facebookCallback

    private val _signInStatus = MutableLiveData<SignInStatus?>()
    val signInStatus: LiveData<SignInStatus?>
        get() = _signInStatus


    //////google sign in
    fun googleSignIn() {
        _signInStatus.value =
            SignInStatus.STARTED
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.server_client_id))
                .requestEmail()
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = mGoogleSignInClient.signInIntent

        _googleSignInIntent.value = signInIntent
    }

    fun handelGoogleToken(idToken: String) {
        //start coroutines job to auth google user with firebase auth
        coroutineScope.launch {
            Log.e("debug: entryViewModel", "iam in coroutine")

            val job = withTimeoutOrNull(TIME_MILLISECOND) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth(credential)
                Log.e("debug: entryViewModel", "iam in timed job")
            }
            //time out job is null
            if (job == null) {
                _signInStatus.postValue(SignInStatus.CONNECTION_TIME_OUT)
                Log.e("debug: entryViewModel", "job time out")
            }
        }
    }


    ///////facebook sign in
    fun facebookSignIn() {
        _signInStatus.value =
            SignInStatus.STARTED
        callbackManager = CallbackManager.Factory.create()
        _facebookCallback.value = callbackManager
    }

    fun handleFacebookAccessToken(accessToken: AccessToken) {
        coroutineScope.launch {
            Log.e("debug: entryViewModel", "iam in coroutine")

            val job = withTimeoutOrNull(TIME_MILLISECOND) {
                val credential = FacebookAuthProvider.getCredential(accessToken.token)
                firebaseAuth(credential)
                Log.e("debug: entryViewModel", "iam in timed job")
            }
            //time out job is null
            if (job == null) {
                _signInStatus.postValue(SignInStatus.CONNECTION_TIME_OUT)
                Log.e("debug: entryViewModel", "job time out")
            }
        }
    }


    ///////email sign up
    fun emailSignUp(email: String, password: String) {
        _signInStatus.value =
            SignInStatus.STARTED
        Log.e("debug: entryViewModel", "iam in coroutine")

        coroutineScope.launch {
            val job = withTimeoutOrNull(TIME_MILLISECOND) {
                Log.e("debug: entryViewModel", "iam in timed job")
                firebaseEmailSignUp(email, password)
            }

            if (job == null) {
                _signInStatus.postValue(SignInStatus.CONNECTION_TIME_OUT)
                Log.e("debug: entryViewModel", "job time out")
            }
        }
    }

    private suspend fun firebaseEmailSignUp(email: String, password: String) {
        //try to auth user in firebase with google credential
        firebaseAuth = FirebaseAuth.getInstance()
        Log.e("debug: entryViewModel", "iam in firebaseEmailSignup")


        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val firebaseUser = firebaseAuth.currentUser!!
            Log.e("debug: entryViewModel", "the user uid ${firebaseUser.uid}")

            sendUserInfoToServer(firebaseUser)

        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("error: entryViewModel", "firebaseAuth error", e)
            ifErrorSignOutAndCancel()
            _signInStatus.postValue(SignInStatus.USER_COLLISION_ERROR)

        }catch (e: FirebaseNetworkException) {
            ifErrorSignOutAndCancel()
            _signInStatus.postValue(SignInStatus.ERROR)
        }
        catch (e: Exception) {
            Log.e("error: entryViewModel", "firebaseAuth error", e)
            ifErrorSignOutAndCancel()
            _signInStatus.postValue(SignInStatus.ERROR)
        }
    }


    ///////email sign in
    fun emailSignIn(email: String, password: String) {
        _signInStatus.value =
            SignInStatus.STARTED
        Log.e("debug: entryViewModel", "iam in coroutine")

        coroutineScope.launch {
            val job = withTimeoutOrNull(TIME_MILLISECOND) {
                firebaseEmailSignIn(email, password)
                Log.e("debug: entryViewModel", "iam in timed job")
            }

            if (job == null) {
                _signInStatus.postValue(SignInStatus.CONNECTION_TIME_OUT)
                Log.e("debug: entryViewModel", "job time out")
            }
        }
    }

    private suspend fun firebaseEmailSignIn(email: String, password: String) {
        //try to auth user in firebase with google credential
        firebaseAuth = FirebaseAuth.getInstance()

        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = firebaseAuth.currentUser!!

            sendUserInfoToServer(firebaseUser)

        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("error: entryViewModel", "firebaseAuth error", e)
            _signInStatus.postValue(SignInStatus.USER_COLLISION_ERROR)
            ifErrorSignOutAndCancel()

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            _signInStatus.postValue(SignInStatus.CREDENTIAL_ERROR)
            ifErrorSignOutAndCancel()
        }
        catch (e: FirebaseNetworkException) {
            _signInStatus.postValue(SignInStatus.ERROR)
            ifErrorSignOutAndCancel()
        }
        catch (e: Exception) {
            Log.e("error: entryViewModel", "firebaseAuth error", e)
            _signInStatus.postValue(SignInStatus.ERROR)
            ifErrorSignOutAndCancel()
        }
    }

    private suspend fun firebaseAuth(credential: AuthCredential) {

        //try to auth user in firebase with google credential
        firebaseAuth = FirebaseAuth.getInstance()

        try {
            firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = firebaseAuth.currentUser!!

            sendUserInfoToServer(firebaseUser)

        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("error: entryViewModel", "firebaseAuth error", e)

            ifErrorSignOutAndCancel()

            _signInStatus.postValue(SignInStatus.USER_COLLISION_ERROR)
        } catch (e: Exception) {

            Log.e("error: entryViewModel", "firebaseAuth error", e)

            ifErrorSignOutAndCancel()

            _signInStatus.postValue(SignInStatus.ERROR)
        }
    }

    private suspend fun sendUserInfoToServer(firebaseUser: FirebaseUser) {
        //add or get user from the server
        usersApiServices = NetworkCall.usersService

        try {
            Log.e("debug: entryViewModel", "trying to send user to server")

            val serverResponse = usersApiServices.addNewUserToDatabaseAsync(
                ServerUser(
                    -555,
                    firebaseUser.uid, firebaseUser.email!!, UserType.COMPANY, null
                )
            ).await()

            when (serverResponse.responseCode) {
                ResponseCode.SUCCESS -> {
                    UserPreferences.setUserInfo(context, serverResponse.body)
                    _signInStatus.postValue(SignInStatus.COMPLETE)
                }
                ResponseCode.FIREBASE_CODE_NOT_VALID -> _signInStatus.postValue(SignInStatus.FIREBASE_UID_ERROR)
                else -> _signInStatus.postValue(SignInStatus.ERROR)
            }
        } catch (e: SocketTimeoutException) {
            Log.e("error", "error", e)

            ifErrorSignOutAndCancel()

            //inform the user that is an error while uploading info
            _signInStatus.postValue(SignInStatus.CONNECTION_TIME_OUT)
        } catch (e: Exception) {
            Log.e("error", "error", e)

            ifErrorSignOutAndCancel()

            //inform the user that is an error while uploading info
            _signInStatus.postValue(SignInStatus.ERROR)
        }
    }

    fun restoreSignInStatus() {
        _signInStatus.value = null
    }

    private fun ifErrorSignOutAndCancel() {
        firebaseAuth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}