package com.shouman.apps.reseller.admin.ui.main.newCustomerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shouman.apps.reseller.admin.R

class NewCustomerFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: NewCustomerkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_customer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewCustomerkViewModel::class.java)
        // TODO: Use the ViewModel
    }

}