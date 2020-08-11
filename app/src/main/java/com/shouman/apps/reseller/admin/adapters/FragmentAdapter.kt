package com.shouman.apps.reseller.admin.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shouman.apps.reseller.admin.ui.main.customersFragment.CustomersFragment
import com.shouman.apps.reseller.admin.ui.main.homeFragment.HomeFragment
import com.shouman.apps.reseller.admin.ui.main.profileFragment.ProfileFragment
import com.shouman.apps.reseller.admin.ui.main.reportsFragment.ReportsFragment
import com.shouman.apps.reseller.admin.ui.main.teamFragment.TeamFragment
import java.lang.IllegalArgumentException

class FragmentAdapter(fragmentActivity: FragmentActivity) :FragmentStateAdapter(fragmentActivity) {
    private val ITEM_COUNT = 5
    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment()
            1-> TeamFragment()
            2 -> CustomersFragment()
            3 -> ReportsFragment()
            4 -> ProfileFragment()
            else -> throw IllegalArgumentException("no such fragment")
        }
    }


}