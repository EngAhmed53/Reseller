package com.shouman.apps.reseller.admin.ui.main.teamFragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.BranchesListAdapter
import com.shouman.apps.reseller.admin.databinding.TeamFragmentBinding

class TeamFragment : Fragment() {

    companion object {
        fun newInstance() = TeamFragment()
    }

    private lateinit var mBinding:TeamFragmentBinding

    private lateinit var viewModel: TeamViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = TeamFragmentBinding.inflate(inflater)

        viewModel = ViewModelProvider(this).get(TeamViewModel::class.java)

        mBinding.lifecycleOwner = this

        mBinding.teamViewModel = viewModel

        mBinding.branchesRec.adapter = BranchesListAdapter()

        return mBinding.root
    }
}