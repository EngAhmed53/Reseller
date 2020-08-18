package com.shouman.apps.reseller.admin.ui.main.newVisitFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.gms.maps.model.LatLng
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.SpinnerArrayAdapter
import com.shouman.apps.reseller.admin.data.model.DatabaseCustomer
import com.shouman.apps.reseller.admin.databinding.NewVisitFragmentBinding

class NewVisitFragment : Fragment() {

    private val viewModel: NewVisitViewModel by navGraphViewModels(R.id.main_nav)
    private lateinit var mBinding: NewVisitFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = NewVisitFragmentBinding.inflate(inflater)
        mBinding.lifecycleOwner = this

        mBinding.newVisitViewModel = viewModel
        mBinding.visit = viewModel.visit

        viewModel.visitLiveData.observe(viewLifecycleOwner, Observer { visit ->
            visit?.let {
                val toMap =
                    NewVisitFragmentDirections.actionNewVisitFragmentToFragmentNewVisitLocationPicker(
                        visit,
                        LatLng(
                            viewModel.selectedCustomer!!.latitude,
                            viewModel.selectedCustomer!!.longitude
                        )
                    )
                findNavController().navigate(toMap)
                viewModel.restoreVisitState()
            }
        })

        mBinding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.filledExposedDropdown.setAdapter(
            SpinnerArrayAdapter<DatabaseCustomer>(
                requireContext(),
                ArrayList()
            )
        )



        return mBinding.root
    }


}