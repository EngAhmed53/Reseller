package com.shouman.apps.reseller.admin.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val _fabAction = MediatorLiveData<Boolean?>()
    val fabAction: LiveData<Boolean?>
    get() = _fabAction



}