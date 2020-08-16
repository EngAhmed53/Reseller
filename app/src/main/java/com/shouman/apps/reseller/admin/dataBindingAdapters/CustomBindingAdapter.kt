package com.shouman.apps.reseller.admin.dataBindingAdapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.BranchesListAdapter
import com.shouman.apps.reseller.admin.adapters.CustomersPagedListAdapter
import com.shouman.apps.reseller.admin.adapters.SpinnerArrayAdapter
import com.shouman.apps.reseller.admin.api.PageableCustomer
import com.shouman.apps.reseller.admin.data.model.MiniDatabaseBranch
import com.shouman.apps.reseller.admin.domain.DomainBranchSalesmen
import com.shouman.apps.reseller.admin.ui.auth.completeUserInfo.CompleteUserInfoViewModel
import com.shouman.apps.reseller.admin.ui.auth.completeUserInfo.UploadInfoStatus
import com.shouman.apps.reseller.admin.ui.auth.entryScreen.SignInStatus
import com.shouman.apps.reseller.admin.ui.main.newSalesmanFragment.NewSalesmanViewModel
import com.shouman.apps.reseller.admin.ui.main.teamFragment.DataStatus
import com.shouman.apps.reseller.admin.utils.getRandomColor


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
        UploadInfoStatus.START_COMPANY_INFO_UPLOAD -> {
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
fun CountryCodePicker.setCompletePhoneNumber(
    phoneNumber: String?,
    viewModel: CompleteUserInfoViewModel
) {
    phoneNumber.let {
        println("full num is $fullNumberWithPlus")
        viewModel.setCompletePhoneNumber(fullNumberWithPlus)
    }
}

@BindingAdapter("setBranchesList")
fun RecyclerView.setBranchesList(branchesList: List<DomainBranchSalesmen>?) {
    branchesList?.let {
        val adapter = adapter as BranchesListAdapter
        adapter.submitList(branchesList)
    }
}

@BindingAdapter("setRecVisibility")
fun RecyclerView.setBranchesRecVisibility(dataStatus: DataStatus?) {
    visibility = when (dataStatus) {
        null,
        DataStatus.LOCAL_EMPTY,
        DataStatus.SERVER_EMPTY,
        DataStatus.ERROR -> View.INVISIBLE

        DataStatus.FETCHED -> View.VISIBLE
    }
}


@BindingAdapter("setProVisibility")
fun ProgressBar.setBranchesProgressVisibility(dataStatus: DataStatus?) {
    visibility = when (dataStatus) {
        null,
        DataStatus.LOCAL_EMPTY -> View.VISIBLE

        DataStatus.SERVER_EMPTY,
        DataStatus.ERROR,
        DataStatus.FETCHED -> View.INVISIBLE
    }
}


@BindingAdapter("setTextVisibility")
fun TextView.setBranchesErrorTextVisibility(dataStatus: DataStatus?) {
    visibility = when (dataStatus) {
        DataStatus.ERROR -> View.VISIBLE

        null,
        DataStatus.LOCAL_EMPTY,
        DataStatus.SERVER_EMPTY,
        DataStatus.FETCHED -> View.INVISIBLE
    }
}


@BindingAdapter(value = ["miniBranchesList", "newSalesViewModel"])
fun AppCompatAutoCompleteTextView.setMiniBranchesList(
    miniBranchesList: List<MiniDatabaseBranch>?,
    newSalesViewModel: NewSalesmanViewModel
) {
    miniBranchesList?.let {
        val adapter = adapter as SpinnerArrayAdapter<MiniDatabaseBranch>
        adapter.addAll(*it.toTypedArray())

        setOnItemClickListener { _, _, position, _ ->
            setText(miniBranchesList[position].name, false)
            newSalesViewModel.selectedBranchId.value = miniBranchesList[position].id
        }
    }
}

@BindingAdapter("setBackgroundTint")
fun FrameLayout.setBackgroundTint(position: Int?) {
    position?.let {
        backgroundTintList = ColorStateList.valueOf(Color.parseColor(getRandomColor(position)))
    }
}


@BindingAdapter("setCustomersList")
fun RecyclerView.setCustomersList(customersList: PagedList<PageableCustomer>?) {
    customersList?.let {
        val adapter = adapter as CustomersPagedListAdapter
        adapter.submitList(customersList)
    }
}
