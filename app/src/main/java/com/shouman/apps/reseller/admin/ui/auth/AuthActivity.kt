package com.shouman.apps.reseller.admin.ui.auth

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityAuthBinding>(this, R.layout.activity_auth)
    }
}
