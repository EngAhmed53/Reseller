package com.shouman.apps.reseller.admin.ui.main.newBranchFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.FragmentNewBranchBinding




class NewBranchFragment : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentNewBranchBinding
    private lateinit var branchViewMode:NewBranchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentNewBranchBinding.inflate(inflater)

        branchViewMode = ViewModelProvider(this).get(NewBranchViewModel::class.java)

        mBinding.branchViewModel = branchViewMode
        mBinding.lifecycleOwner = this

        mBinding.materialToolbar.setNavigationOnClickListener {
            dismiss()
        }

        branchViewMode.newBranchStatus.observe(viewLifecycleOwner, Observer { newBranchStatus ->
            when (newBranchStatus) {
                NewBranchStatus.COMPLETED -> {
                    val contextView = dialog!!.window!!.decorView
                    Snackbar.make(contextView, getString(R.string.saved_successfully), Snackbar.LENGTH_LONG)
                        .show()
                    branchViewMode.restoreBranchStatus()
                    dismiss()
                }
                NewBranchStatus.ERROR -> {
                    val contextView = dialog!!.window!!.decorView
                    Snackbar.make(contextView, getString(R.string.saving_new_branch_error), Snackbar.LENGTH_LONG)
                        .show()
                    branchViewMode.restoreBranchStatus()
                }
                NewBranchStatus.TIMED_OUT -> {
                    val contextView = dialog!!.window!!.decorView
                    Snackbar.make(
                        contextView, R.string.connection_time_out, Snackbar.LENGTH_SHORT
                    )
                        .show()
                    branchViewMode.restoreBranchStatus()
                }
                else -> return@Observer
            }
        })
        return mBinding.root
    }
}