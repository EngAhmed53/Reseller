package com.shouman.apps.reseller.admin.application

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.database.FirebaseDatabase

class ResellerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
    }
}