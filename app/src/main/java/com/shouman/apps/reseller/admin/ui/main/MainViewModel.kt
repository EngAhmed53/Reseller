package com.shouman.apps.reseller.admin.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _fabAction = MutableLiveData<Boolean?>()
    val fabAction: LiveData<Boolean?>
        get() = _fabAction

    private val _subFabAction = MutableLiveData<Boolean?>()
    val subFabAction: LiveData<Boolean?>
        get() = _subFabAction

    fun doMainFabAction() {
        _fabAction.value = true
    }

    fun doSubActionFab() {
        _subFabAction.value = true
    }

    fun mainFabActionDon() {
        _fabAction.value = null
    }

    fun subFabActionDon() {
        _subFabAction.value = null
    }
}