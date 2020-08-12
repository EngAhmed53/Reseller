package com.shouman.apps.reseller.admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shouman.apps.reseller.admin.databinding.BranchListItemBinding
import com.shouman.apps.reseller.admin.domain.DomainBranchSalesmen


class BranchesListAdapter :
    ListAdapter<DomainBranchSalesmen, BranchesListAdapter.BranchViewHolder>(
        DiffCallback
    ) {

    companion object DiffCallback :
        DiffUtil.ItemCallback<DomainBranchSalesmen>() {
        override fun areItemsTheSame(
            oldItem: DomainBranchSalesmen,
            newItem: DomainBranchSalesmen
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: DomainBranchSalesmen,
            newItem: DomainBranchSalesmen
        ): Boolean {
            return oldItem.databaseBranch == newItem.databaseBranch && oldItem.salesmenList == newItem.salesmenList
        }
    }

    class BranchViewHolder(
        private val mBinding: BranchListItemBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(domainBranchSalesmen: DomainBranchSalesmen) {
            mBinding.branchSalesmen = domainBranchSalesmen

            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        return BranchViewHolder(
            BranchListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
    }
}