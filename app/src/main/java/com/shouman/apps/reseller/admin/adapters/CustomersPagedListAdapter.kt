package com.shouman.apps.reseller.admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shouman.apps.reseller.admin.api.PageableCustomer
import com.shouman.apps.reseller.admin.databinding.CustomerListItemBinding

class CustomersPagedListAdapter :
    PagedListAdapter<PageableCustomer, CustomersPagedListAdapter.CustomerViewHolder>(
        CustomerDiffUtil
    ) {

    class CustomerViewHolder(val mBinding: CustomerListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(pageableCustomer: PageableCustomer) {
            mBinding.customer = pageableCustomer
        }
    }


    companion object CustomerDiffUtil : DiffUtil.ItemCallback<PageableCustomer>() {
        override fun areItemsTheSame(
            oldItem: PageableCustomer,
            newItem: PageableCustomer
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: PageableCustomer,
            newItem: PageableCustomer
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view =  CustomerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer!!)
        holder.mBinding.position = position
    }


}