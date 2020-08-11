package com.shouman.apps.reseller.admin.dataBindingAdapters

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.ui.auth.completeUserInfo.CompleteUserInfoViewModel
import com.shouman.apps.reseller.admin.ui.auth.completeUserInfo.UploadInfoStatus
import com.shouman.apps.reseller.admin.ui.auth.entryScreen.SignInStatus


@BindingAdapter("status")
fun FrameLayout.status(status: SignInStatus?) {
    println("sign in status -> $status")
    when (status) {
        SignInStatus.STARTED -> {
            startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.fade_in_animation
                )
            )
            visibility = View.VISIBLE
            isClickable = true
            isFocusable = true
        }
        else -> {
            visibility = View.GONE
            isClickable = false
            isFocusable = false
        }
    }
}

@BindingAdapter("uploadInfoStatus")
fun FrameLayout.uploadCompanyInfoStatus(status: UploadInfoStatus?) {
    when (status) {
        //show the uploading progressBar
        UploadInfoStatus.START_PHOTO_UPLOAD,
        UploadInfoStatus.START_COMPANY_INFO_UPLOAD ->{
            if (isVisible) return
            startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.fade_in_animation
                )
            )
            visibility = View.VISIBLE
            isClickable = true
            isFocusable = true
        }

        else -> {
            visibility = View.GONE
            isClickable = false
            isFocusable = false
        }
    }
}

@BindingAdapter("uploadInfoTextStatus")
fun TextView.uploadCompanyInfoStatus(status: UploadInfoStatus?) {
    text = when (status) {
        UploadInfoStatus.START_PHOTO_UPLOAD -> {
            context.getString(R.string.uploading_company_logo)
        }
        UploadInfoStatus.START_COMPANY_INFO_UPLOAD -> {
            context.getString(R.string.uploading_company_info)
        }
        else -> return
    }
}


@BindingAdapter("setEndIconActive")
fun TextInputLayout.setCheckMarkActive(isTextValid: Boolean) {
    println(isTextValid)
    isEndIconVisible = isTextValid
}

@BindingAdapter(value = ["bind:phoneNumber", "bind:viewModel"])
fun CountryCodePicker.setCompletePhoneNumber(phoneNumber:String?, viewModel: CompleteUserInfoViewModel) {
    phoneNumber.let {
        println("full num is $fullNumberWithPlus")
        viewModel.setCompletePhoneNumber(fullNumberWithPlus)
    }
}