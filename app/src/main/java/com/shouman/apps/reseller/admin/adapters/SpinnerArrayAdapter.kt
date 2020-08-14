package com.shouman.apps.reseller.admin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.data.model.MiniDatabaseBranch

class SpinnerArrayAdapter<T>(context: Context, val list: List<T>) :
    ArrayAdapter<T>(context, 0, list) {


    private val mContext = context


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (convertView == null) view =
            LayoutInflater.from(mContext).inflate(R.layout.material_spinner_item, parent, false)

        if (!list.isNullOrEmpty()) {
            when (val item = getItem(position)) {
                is MiniDatabaseBranch -> {
                    view!!.findViewById<TextView>(R.id.material_textView).text =
                        (item as MiniDatabaseBranch).name
                }
            }
        }
        return view!!
    }
}