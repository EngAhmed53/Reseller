package com.shouman.apps.reseller.admin.ui.main.homeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shouman.apps.reseller.admin.adapters.ActivitiesPagedListAdapter
import com.shouman.apps.reseller.admin.databinding.HomeFragment3Binding
import com.shouman.apps.reseller.admin.databinding.HomeFragment4Binding
import com.shouman.apps.reseller.admin.repository.paging.companyCustomers.DataStatus

class HomeFragment : Fragment() {

    private lateinit var mBinding: HomeFragment4Binding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = HomeFragment4Binding.inflate(inflater)

        mBinding.apply {
            viewModel = homeViewModel
            lifecycleOwner = this@HomeFragment

            activitiesRecView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = ActivitiesPagedListAdapter()
                setHasFixedSize(true)
            }
            infoFrame.setOnRefreshListener {
                homeViewModel.getSelectedDateSummary(homeViewModel.selectedDayLiveData.value!!)
            }
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.dataStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it != DataStatus.FETCHING) {
                    println("not fetching")
                    mBinding.infoFrame.isRefreshing = false
                }
            }
        })

        homeViewModel.daySummaryLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                mBinding.dateSummary = it
            }
        })
    }

}