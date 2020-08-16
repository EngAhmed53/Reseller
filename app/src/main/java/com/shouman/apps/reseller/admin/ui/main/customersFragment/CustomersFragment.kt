package com.shouman.apps.reseller.admin.ui.main.customersFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shouman.apps.reseller.admin.adapters.CustomersPagedListAdapter
import com.shouman.apps.reseller.admin.databinding.CustomersFragmentBinding
import com.shouman.apps.reseller.admin.repository.paging.DataStatus

class CustomersFragment : Fragment() {

    private lateinit var mBinding: CustomersFragmentBinding
    private lateinit var viewModel: CustomersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = CustomersFragmentBinding.inflate(inflater)

        viewModel = ViewModelProvider(this).get(CustomersViewModel::class.java)

        mBinding.lifecycleOwner = this

        mBinding.customersViewModel = viewModel

        mBinding.customersRec.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = CustomersPagedListAdapter()
            setHasFixedSize(true)
        }

        mBinding.swipeView.setOnRefreshListener {
            viewModel.pagedLiveData.value!!.dataSource.invalidate()
        }

        viewModel.dataStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it != DataStatus.FETCHING) {
                    mBinding.swipeView.isRefreshing = false
                }
            }
        })

        return mBinding.root
    }
}