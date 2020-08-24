package com.shouman.apps.reseller.admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shouman.apps.reseller.admin.api.PageableActivity
import com.shouman.apps.reseller.admin.databinding.ActivityListItemBinding

class ActivitiesPagedListAdapter :
    PagedListAdapter<PageableActivity, ActivitiesPagedListAdapter.ActivityViewHolder>(
        CustomerDiffUtil
    ) {

    class ActivityViewHolder(val mBinding: ActivityListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(pageableActivity: PageableActivity) {
            mBinding.activity = pageableActivity
        }
    }

    companion object CustomerDiffUtil : DiffUtil.ItemCallback<PageableActivity>() {
        override fun areItemsTheSame(
            oldItem: PageableActivity,
            newItem: PageableActivity
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: PageableActivity,
            newItem: PageableActivity
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view =
            ActivityListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer!!)
    }


//    class OnCustomerClickListener(val clickListener: (customerId: Long) -> Unit) {
//        fun onClick(customerId: Long) = clickListener(customerId)
//    }
}