package com.shouman.apps.reseller.admin.ui.auth

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.shouman.apps.reseller.admin.databinding.FragmentSplashScreenBinding

/**
 * A simple [Fragment] subclass.
 */
class SplashScreen : Fragment() {

    private lateinit var mBinding: FragmentSplashScreenBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSplashScreenBinding.inflate(inflater)

        //hide status bar
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        initViewModel()
        val handler = Handler()
        handler.postDelayed({ authViewModel.setNavPath() }, 2000)
        return mBinding.root
    }

    private fun initViewModel() {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel.path.observe(viewLifecycleOwner, Observer { path ->
            path?.let {
                when (it) {
                    Path.FIREBASE_USER_IS_NULL -> navigateToSignUp()
                    Path.FIREBASE_USER_IS_NOT_NULL_BUT_PREFERENCE_NOT_SETTED -> navigateToProfileSetUp()
                    Path.FIREBASE_USER_IS_NOT_NULL_AND_PREFERENCE_SETTED -> navigateToMain()
                }
            }
        })
    }

    private fun navigateToMain() {
        val toMainActivity = SplashScreenDirections.actionToMain()
        findNavController().navigate(toMainActivity)
        requireActivity().finish()
    }

    private fun navigateToProfileSetUp() {
        val toProfileFragment =
            SplashScreenDirections.actionFragmentSplashScreenToCompleteUserInfoFragment()
        findNavController().navigate(toProfileFragment)

    }

    private fun navigateToSignUp() {
        val toEntryScreen = SplashScreenDirections.actionFragmentSplashScreenToEntryScreenFragment()
        findNavController().navigate(toEntryScreen)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

}
