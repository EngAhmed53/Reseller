package com.shouman.apps.reseller.admin.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shouman.apps.reseller.admin.R


/**
 *this [TransparentFragment] dose not do any thing and dose not have any logic
 * its just for using as a entry point and start destination for thr  [main-nav] graph
 */
class TransparentFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transparent, container, false)
    }
}