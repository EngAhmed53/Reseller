package com.shouman.apps.reseller.admin.ui.auth.entryScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.FragmentEntryScreenBinding
import com.shouman.apps.reseller.admin.ui.auth.*


/**
 * A simple [Fragment] subclass.
 */
class EntryScreenFragment : Fragment() {

    private lateinit var mCallbackManager: CallbackManager
    private val RC_SIGN_IN: Int = 522
    private lateinit var mBinding: FragmentEntryScreenBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var entryScreenViewModel: EntryScreenViewModel
    private var email: String? = null
    private val password: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEntryScreenBinding.inflate(inflater)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        entryScreenViewModel = ViewModelProvider(this).get(EntryScreenViewModel::class.java)

        mBinding.lifecycleOwner = this
        mBinding.entryScreenViewModel = entryScreenViewModel
        mBinding.email = email
        mBinding.password = password


        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.path.observe(viewLifecycleOwner, Observer { path ->
            path?.let {
                when (it) {
                    Path.FIREBASE_USER_IS_NOT_NULL_BUT_PREFERENCE_NOT_SETTED -> navigateToProfileSetUp()
                    Path.FIREBASE_USER_IS_NOT_NULL_AND_PREFERENCE_SETTED -> navigateToMain()
                    else -> return@let
                }
            }
        })

        entryScreenViewModel.googleSignInIntent.observe(viewLifecycleOwner, Observer { intent ->
            intent?.let {
                startActivityForResult(intent, RC_SIGN_IN)
            }
        })

        entryScreenViewModel.facebookCallback.observe(
            viewLifecycleOwner, Observer { callBackManager ->

                callBackManager?.let {
                    mCallbackManager = callBackManager
                    val loginManager = LoginManager.getInstance()
                    loginManager.logInWithReadPermissions(
                        this,
                        arrayListOf("email", "public_profile")
                    )

                    loginManager.registerCallback(callBackManager,
                        object : FacebookCallback<LoginResult> {
                            override fun onSuccess(result: LoginResult?) {
                                result?.let {
                                    Log.e("error: onCancel", "sign with facebook success")
                                    entryScreenViewModel.handleFacebookAccessToken(result.accessToken)
                                    entryScreenViewModel.restoreSignInStatus()
                                }
                            }

                            override fun onCancel() {
                                val contextView = mBinding.googleCardView
                                Snackbar.make(
                                    contextView,
                                    R.string.sign_in_canceled,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                entryScreenViewModel.restoreSignInStatus()
                                Log.e("error: onCancel", "sign with facebook canceled")
                            }

                            override fun onError(error: FacebookException?) {
                                val contextView = mBinding.googleCardView
                                Snackbar.make(
                                    contextView,
                                    R.string.sign_in_failed,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                entryScreenViewModel.restoreSignInStatus()
                                Log.e("error: onError", "sign with facebook error", error)
                            }

                        })
                }

            })

        entryScreenViewModel.signInStatus.observe(viewLifecycleOwner, Observer { status ->

            status?.let {
                when (status) {
                    SignInStatus.STARTED -> return@let

                    SignInStatus.COMPLETE -> {
                        //sign in completed successfully define the new auth path to go to
                        authViewModel.setNavPath()
                        entryScreenViewModel.restoreSignInStatus()
                    }

                    SignInStatus.ERROR -> {
                        val contextView = mBinding.googleCardView
                        Snackbar.make(contextView, R.string.sign_in_failed, Snackbar.LENGTH_LONG)
                            .show()
                        entryScreenViewModel.restoreSignInStatus()
                    }

                    SignInStatus.FIREBASE_UID_ERROR -> {
                        val contextView = mBinding.googleCardView
                        Snackbar.make(contextView, R.string.firebase_uid_error, Snackbar.LENGTH_LONG)
                            .show()
                        entryScreenViewModel.restoreSignInStatus()
                    }

                    SignInStatus.USER_COLLISION_ERROR -> {
                        val contextView = mBinding.googleCardView
                        Snackbar.make(
                            contextView,
                            R.string.error_user_collision,
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                        entryScreenViewModel.restoreSignInStatus()
                    }

                    SignInStatus.CREDENTIAL_ERROR -> {
                        val contextView = mBinding.googleCardView
                        Snackbar.make(contextView, R.string.credential_error, Snackbar.LENGTH_LONG)
                            .show()
                        entryScreenViewModel.restoreSignInStatus()
                    }


                    SignInStatus.CONNECTION_TIME_OUT -> {
                        val contextView = mBinding.googleCardView
                        Snackbar.make(
                            contextView, R.string.connection_time_out, Snackbar.LENGTH_SHORT
                        )
                            .show()
                        entryScreenViewModel.restoreSignInStatus()
                    }
                }
            }
        })
    }

    private fun navigateToMain() {
        val toMain =
            EntryScreenFragmentDirections.actionEntryScreenFragmentToMainActivity()
        findNavController().navigate(toMain)
    }

    private fun navigateToProfileSetUp() {
        val toProfileSetUp =
            EntryScreenFragmentDirections.actionEntryScreenFragmentToCompleteUserInfoFragment()
        findNavController().navigate(toProfileSetUp)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("debug: entryScreen", "firebaseAuthWithGoogle:" + account.id)
                entryScreenViewModel.handelGoogleToken(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("debug: entryScreen", "Google sign in failed", e)
                entryScreenViewModel.restoreSignInStatus()
                val contextView = mBinding.googleCardView
                Snackbar.make(contextView, R.string.sign_in_failed, Snackbar.LENGTH_LONG).show()
            }
        } else mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
