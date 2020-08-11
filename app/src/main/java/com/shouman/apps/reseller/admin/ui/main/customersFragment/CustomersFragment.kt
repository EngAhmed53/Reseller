package com.shouman.apps.reseller.admin.ui.main.customersFragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shouman.apps.reseller.admin.R

class CustomersFragment : Fragment() {

    companion object {
        fun newInstance() = CustomersFragment()
    }

    private lateinit var viewModel: CustomersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.customers_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CustomersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}