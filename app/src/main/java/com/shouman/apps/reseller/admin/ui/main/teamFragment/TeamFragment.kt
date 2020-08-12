package com.shouman.apps.reseller.admin.ui.main.teamFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shouman.apps.reseller.admin.adapters.BranchesListAdapter
import com.shouman.apps.reseller.admin.databinding.TeamFragmentBinding

class TeamFragment : Fragment() {

    private lateinit var mBinding: TeamFragmentBinding

    private lateinit var viewModel: TeamViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = TeamFragmentBinding.inflate(inflater)

        viewModel = ViewModelProvider(this).get(TeamViewModel::class.java)

        mBinding.lifecycleOwner = this

        mBinding.teamViewModel = viewModel

        mBinding.branchesRec.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = BranchesListAdapter()
            setHasFixedSize(true)
        }

        viewModel.branchesAndSalesmen.observe(viewLifecycleOwner, Observer {
            viewModel.checkIfLocalDataAvailable()
        })

        return mBinding.root
    }
}