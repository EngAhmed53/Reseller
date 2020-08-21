package com.shouman.apps.reseller.admin.ui.main.customerProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.CustomerProfileFragmentBinding

class CustomerProfileFragment : Fragment() {

    private lateinit var viewModel: CustomerProfileViewModel
    private lateinit var mBinding: CustomerProfileFragmentBinding
    private var customerId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customerId = CustomerProfileFragmentArgs.fromBundle(requireArguments()).customerId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = CustomerProfileFragmentBinding.inflate(inflater)

        val factory = CustomerProfileViewModelFactory(requireActivity().application, customerId)
        viewModel = ViewModelProvider(this, factory).get(CustomerProfileViewModel::class.java)

        mBinding.apply {
            customerProfileViewModel = viewModel

            lifecycleOwner = this@CustomerProfileFragment

        }

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customerProfileLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                mBinding.customer = it
            }
        })


        viewModel.getCustomerProfileStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                GetCustomerProfileStatus.CUSTOMER_ID_NOT_VALID -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.customer_id_not_valid),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    viewModel.restoreCustomerProfileStatus()
                }
                GetCustomerProfileStatus.COMPANY_ID_NOT_VALID -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.company_id_not_valid),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    viewModel.restoreCustomerProfileStatus()
                }

                GetCustomerProfileStatus.TIMED_OUT -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.connection_time_out),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    viewModel.restoreCustomerProfileStatus()
                }
                GetCustomerProfileStatus.ERROR -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.no_internet_connection),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    viewModel.restoreCustomerProfileStatus()
                }
                else -> return@Observer
            }
        })


        viewModel.makePhoneCallIntent.observe(viewLifecycleOwner, Observer {
            it?.let { intent ->
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
                viewModel.restorePhoneCallIntent()
            }
        })
        viewModel.openLocationOnMap.observe(viewLifecycleOwner, Observer {
            it?.let { intent ->
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
                viewModel.restoreMapIntent()
            }
        })

    }
}