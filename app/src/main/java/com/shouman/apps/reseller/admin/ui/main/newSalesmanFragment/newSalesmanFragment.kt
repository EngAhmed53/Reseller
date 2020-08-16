package com.shouman.apps.reseller.admin.ui.main.newSalesmanFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.SpinnerArrayAdapter
import com.shouman.apps.reseller.admin.data.model.MiniDatabaseBranch
import com.shouman.apps.reseller.admin.databinding.FragmentNewSalesmanBinding

class NewSalesmanFragment : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentNewSalesmanBinding
    private lateinit var newSalesmanViewModel: NewSalesmanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentNewSalesmanBinding.inflate(inflater)

        newSalesmanViewModel = ViewModelProvider(this).get(NewSalesmanViewModel::class.java)

        mBinding.newSalesViewModel = newSalesmanViewModel
        mBinding.lifecycleOwner = this

        mBinding.materialToolbar.setNavigationOnClickListener {
            dismiss()
        }

        mBinding.filledExposedDropdown.setAdapter(
            SpinnerArrayAdapter<MiniDatabaseBranch>(
                requireContext(),
                ArrayList()
            )
        )

        newSalesmanViewModel.invitationLinkBuildStatus.observe(viewLifecycleOwner, Observer { invitationStatus ->
            when (invitationStatus) {
                InvitationLinkBuildStatus.ERROR -> {
                    val contextView = dialog!!.window!!.decorView
                    Snackbar.make(contextView, getString(R.string.building_invite_link_failed), Snackbar.LENGTH_LONG)
                        .show()
                    newSalesmanViewModel.restoreInvitationState()
                }
                InvitationLinkBuildStatus.TIMED_OUT -> {
                    val contextView = dialog!!.window!!.decorView
                    Snackbar.make(
                        contextView, R.string.connection_time_out, Snackbar.LENGTH_SHORT
                    )
                        .show()
                    newSalesmanViewModel.restoreInvitationState()
                }
                else -> return@Observer
            }
        })

        newSalesmanViewModel.shareIntent.observe(viewLifecycleOwner, Observer {intent ->
            if (intent != null) {
                startActivity(intent)
                newSalesmanViewModel.restoreIntentState()
            }
        })

        return mBinding.root
    }
}