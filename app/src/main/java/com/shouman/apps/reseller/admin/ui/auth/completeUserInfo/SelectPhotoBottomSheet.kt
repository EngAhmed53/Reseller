package com.shouman.apps.reseller.admin.ui.auth.completeUserInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shouman.apps.reseller.admin.databinding.FragmentSelectPhotoBottomSheetBinding
import com.shouman.apps.reseller.admin.ui.auth.AuthActivity

class SelectPhotoBottomSheet : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentSelectPhotoBottomSheetBinding
    private lateinit var userInfoViewModel: CompleteUserInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentSelectPhotoBottomSheetBinding.inflate(inflater)
        userInfoViewModel =
            ViewModelProvider(requireActivity() as AuthActivity).get(CompleteUserInfoViewModel::class.java)

        mBinding.userInfoViewModel = userInfoViewModel
        mBinding.lifecycleOwner = this

        return mBinding.root
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }
}