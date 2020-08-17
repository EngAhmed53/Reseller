package com.shouman.apps.reseller.admin.ui.main.newCustomerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.FragmentNewCustomerBinding

class NewCustomerFragment : Fragment() {

    private val viewModel: NewCustomerViewModel by navGraphViewModels(R.id.main_nav)
    private lateinit var mBinding: FragmentNewCustomerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentNewCustomerBinding.inflate(inflater)



        mBinding.lifecycleOwner = this

        mBinding.newCustomerViewModel = viewModel
        mBinding.customer = viewModel.customer
        mBinding.visit = viewModel.visit

        viewModel.customerMutableLiveData.observe(viewLifecycleOwner, Observer { customer ->
            customer?.let {
                val toPickLocation = NewCustomerFragmentDirections.actionNewCustomerFragmentToFragmentPickLocation(customer)
                findNavController().navigate(toPickLocation)
                viewModel.restoreCustomerObject()
            }
        })

        mBinding.materialToolbar.setNavigationOnClickListener {
         findNavController().popBackStack()
        }

        return mBinding.root
    }
}