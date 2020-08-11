package com.shouman.apps.reseller.admin.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shouman.apps.reseller.admin.preferences.UserPreferences


enum class Path {
    FIREBASE_USER_IS_NULL,
    FIREBASE_USER_IS_NOT_NULL_BUT_PREFERENCE_NOT_SETTED,
    FIREBASE_USER_IS_NOT_NULL_AND_PREFERENCE_SETTED
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val context = application

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var firebaseUser: FirebaseUser? = null

    private val userPreferences by lazy {
        UserPreferences
    }

    private val _path = MutableLiveData<Path>()
    val path: LiveData<Path>
        get() = _path

    fun setNavPath() {
        firebaseUser = firebaseAuth.currentUser
        println(firebaseUser?.uid)
        if (firebaseUser == null) _path.value = Path.FIREBASE_USER_IS_NULL
        else {
            when (userPreferences.getCompanyId(context)) {
                -1L -> _path.value = Path.FIREBASE_USER_IS_NOT_NULL_BUT_PREFERENCE_NOT_SETTED
                else -> _path.value = Path.FIREBASE_USER_IS_NOT_NULL_AND_PREFERENCE_SETTED
            }
        }
    }
}