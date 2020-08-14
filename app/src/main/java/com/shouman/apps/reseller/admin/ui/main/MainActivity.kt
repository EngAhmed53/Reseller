package com.shouman.apps.reseller.admin.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.FragmentAdapter
import com.shouman.apps.reseller.admin.databinding.ActivityMainBinding
import com.shouman.apps.reseller.admin.ui.main.newBranchFragment.NewBranchFragment
import com.shouman.apps.reseller.admin.ui.main.newSalesmanFragment.NewSalesmanFragment

class MainActivity : AppCompatActivity() {

    private var SCREEN_POSITION: Int = 0
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mBinding.lifecycleOwner = this
        mBinding.mainViewModel = mainViewModel


        mBinding.viewPager2.adapter = FragmentAdapter(this)

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2) { tab, position ->
            tab.icon = when (position) {
                0 -> getDrawable(R.drawable.ic_home)
                1 -> getDrawable(R.drawable.ic_supervised_user)
                2 -> getDrawable(R.drawable.ic_customers)
                3 -> getDrawable(R.drawable.ic_baseline_bar)
                4 -> getDrawable(R.drawable.ic_person)
                else -> throw IllegalArgumentException("no such position")
            }
        }.attach()

        mBinding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                SCREEN_POSITION = position
                when (position) {
                    0, 3, 4 -> {
                        mBinding.actionFab.hide()
                        mBinding.subActionFab.hide ()
                    }
                    1 -> {
                        //team main fab
                        teamFragmentMainFab()
                        //team sub fab
                        teamFragmentSubFab()

                    }
                    2 -> {
                        //customers main fab
                        customerFragmentMainFab()
                        //customers sub fab
                        customerFragmentSubFab()
                    }
                    else -> throw IllegalArgumentException("no such position")
                }
            }
        })

        mainViewModel.fabAction.observe(this, Observer {
            if (it == true) {
                when (SCREEN_POSITION) {
                    1 -> addNewBranchFragment()
                    2 -> addNewCustomerFragment()
                    else -> return@Observer
                }
            }
        })

        mainViewModel.subFabAction.observe(this, Observer {
            if (it == true) {
                when (SCREEN_POSITION) {
                    1 -> addNewSalesFragment()
                    2 -> addNewVisitFragment()
                    else -> return@Observer
                }
            }
        })
    }

    private fun customerFragmentSubFab() {
        mBinding.subActionFab.hide()
        mBinding.subActionFab.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.material_green))
        mBinding.subActionFab.setImageResource(R.drawable.ic_redo)
        mBinding.subActionFab.show()
    }

    private fun customerFragmentMainFab() {
        mBinding.actionFab.hide()
        mBinding.actionFab.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorAccent2))
        mBinding.actionFab.setImageResource(R.drawable.ic_person_add)
        mBinding.actionFab.show()
    }

    private fun teamFragmentSubFab() {
        mBinding.subActionFab.hide()
        mBinding.subActionFab.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.material_blue))
        mBinding.subActionFab.setImageResource(R.drawable.ic_add_salesman)
        mBinding.subActionFab.show()
    }

    private fun teamFragmentMainFab() {
        mBinding.actionFab.hide()
        mBinding.actionFab.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        mBinding.actionFab.setImageResource(R.drawable.ic_branch)
        mBinding.actionFab.show()
    }

    private fun addNewVisitFragment() {

    }

    private fun addNewSalesFragment() {
        val newSalesmanFragment = NewSalesmanFragment()
        newSalesmanFragment.show(supportFragmentManager, "newSalesmanFragment")
        mainViewModel.subFabActionDon()
    }

    private fun addNewBranchFragment() {
        val newBranchFragment = NewBranchFragment()
        newBranchFragment.show(supportFragmentManager, "newBranchFragment")
        mainViewModel.mainFabActionDon()
    }

    private fun addNewCustomerFragment() {

    }
}
