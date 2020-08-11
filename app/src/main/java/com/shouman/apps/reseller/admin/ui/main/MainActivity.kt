package com.shouman.apps.reseller.admin.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.FragmentAdapter
import com.shouman.apps.reseller.admin.databinding.ActivityMainBinding
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBinding.viewPager2.adapter = FragmentAdapter(this)

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2) { tab, position ->
            tab.icon = when (position) {
                0 ->  getDrawable(R.drawable.ic_home)
                1 ->  getDrawable(R.drawable.ic_supervised_user)
                2 ->  getDrawable(R.drawable.ic_customers)
                3 -> getDrawable(R.drawable.ic_baseline_bar)
                4 ->    getDrawable(R.drawable.ic_person)
                else -> throw IllegalArgumentException("no such position")
            }
        }.attach()
    }
}
