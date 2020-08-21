package com.shouman.apps.reseller.admin.ui.main.customersFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shouman.apps.reseller.admin.adapters.CustomersPagedListAdapter
import com.shouman.apps.reseller.admin.adapters.CustomersPagedListAdapter.OnCustomerClickListener
import com.shouman.apps.reseller.admin.databinding.CustomersFragmentBinding
import com.shouman.apps.reseller.admin.repository.paging.DataStatus
import com.shouman.apps.reseller.admin.ui.main.TransparentFragmentDirections


class CustomersFragment : Fragment() {

    private lateinit var mBinding: CustomersFragmentBinding
    private lateinit var viewModel: CustomersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = CustomersFragmentBinding.inflate(inflater)

        viewModel = ViewModelProvider(this).get(CustomersViewModel::class.java)

        mBinding.apply {
            lifecycleOwner = this@CustomersFragment
            customersViewModel = viewModel
            customersRec.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = CustomersPagedListAdapter(OnCustomerClickListener {

                    viewModel.displayCustomerProfile(it)

                })
                setHasFixedSize(true)
            }
            swipeView.setOnRefreshListener {
                viewModel.pagedLiveData.value!!.dataSource.invalidate()
            }
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dataStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it != DataStatus.FETCHING) {
                    mBinding.swipeView.isRefreshing = false
                }
            }
        })

        viewModel.navigateToSelectedCustomer.observe(viewLifecycleOwner, Observer {
            it?.let { customerId ->
                val toCustomerProfile =
                    TransparentFragmentDirections.actionTransparentFragmentToCustomerProfileFragment(
                        customerId
                    )
                findNavController().navigate(toCustomerProfile)
                viewModel.restoreSelectedCustomerId()
            }
        })
    }
}