@file:Suppress("UNCHECKED_CAST")

package com.shouman.apps.reseller.admin.dataBindingAdapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.adapters.ActivitiesPagedListAdapter
import com.shouman.apps.reseller.admin.adapters.BranchesListAdapter
import com.shouman.apps.reseller.admin.adapters.CustomersPagedListAdapter
import com.shouman.apps.reseller.admin.adapters.SpinnerArrayAdapter
import com.shouman.apps.reseller.admin.api.ActivityType
import com.shouman.apps.reseller.admin.api.PageableActivity
import com.shouman.apps.reseller.admin.api.PageableCustomer
import com.shouman.apps.reseller.admin.data.model.DatabaseCustomer
import com.shouman.apps.reseller.admin.data.model.MiniDatabaseBranch
import com.shouman.apps.reseller.admin.domain.DomainBranchSalesmen
import com.shouman.apps.reseller.admin.ui.auth.completeUserInfo.CompleteUserInfoViewModel
import com.shouman.apps.reseller.admin.ui.auth.completeUserInfo.UploadInfoStatus
import com.shouman.apps.reseller.admin.ui.auth.entryScreen.SignInStatus
import com.shouman.apps.reseller.admin.ui.main.newCustomerFragment.NewCustomerViewModel
import com.shouman.apps.reseller.admin.ui.main.newSalesmanFragment.NewSalesmanViewModel
import com.shouman.apps.reseller.admin.ui.main.newVisitFragment.NewVisitViewModel
import com.shouman.apps.reseller.admin.ui.main.teamFragment.DataStatus
import com.shouman.apps.reseller.admin.utils.DateUtils
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


@BindingAdapter(value = ["miniBranchesList", "viewModel"])
fun MaterialAutoCompleteTextView.setMiniBranchesList(
    miniBranchesList: List<MiniDatabaseBranch>?,
    viewModel: AndroidViewModel
) {
    miniBranchesList?.let {
        val adapter = adapter as SpinnerArrayAdapter<MiniDatabaseBranch>
        adapter.addAll(it)

        setOnItemClickListener { _, _, position, _ ->
            setText(miniBranchesList[position].name, false)

            when (viewModel) {
                is NewSalesmanViewModel -> viewModel.selectedBranchId.value =
                    miniBranchesList[position].id
                is NewCustomerViewModel -> viewModel.selectedBranchId.value =
                    miniBranchesList[position].id
            }
        }
    }
}


@BindingAdapter(value = ["customersList", "viewModel"])
fun MaterialAutoCompleteTextView.setCustomersList(
    customersList: List<DatabaseCustomer>?,
    viewModel: NewVisitViewModel
) {
    customersList?.let {
        val adapter = adapter as SpinnerArrayAdapter<DatabaseCustomer>
        adapter.addAll(it)
        setOnItemClickListener { _, _, position, _ ->
            val txt =
                "${customersList[position].customerName}, ${customersList[position].businessName}"
            setText(txt, false)

            viewModel.selectedCustomerId.value = customersList[position].id

            viewModel.selectedCustomer = customersList[position]
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
        adapter.submitList(it)
    }
}

@BindingAdapter("setActivitiesList")
fun RecyclerView.setActivitiesList(activitiesList: PagedList<PageableActivity>?) {
    activitiesList?.let {
        val adapter = adapter as ActivitiesPagedListAdapter
        adapter.submitList(it)
    }
}

@BindingAdapter("setWeekDay")
fun TextView.setWeekDay(date: Long?) {
    date?.let {
        text = DateUtils.getWeekDay(context, it)
    }
}

@BindingAdapter("imageUrl")
fun ImageView.bindImage(imgUrl: String?) {

    imgUrl?.let {
        Glide.with(context)
            .load(it)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(this)
        return
    }
    setImageResource(R.drawable.ic_user)
}

@BindingAdapter(value = ["createdBy", "type", "customerName"])
fun TextView.setActivityBodyText(createdBy: String?, type: ActivityType?, customerName: String?) {

    type?.let {

        val creator = createdBy ?: context.getString(R.string.admin)

        text = when (type) {

            ActivityType.NEW_CUSTOMER -> {
                context.getString(R.string.activity_body_new_customer, creator, customerName)
            }
            ActivityType.NEW_VISIT -> {
                context.getString(R.string.activity_body_new_visit, creator, customerName)
            }
        }
    }
}

//@BindingAdapter(value = ["total", "payments", "dept"])
//fun PieChart.setValues(total: Long, payments: Long, dept: Long) {
//    holeRadius = 50f
//    centerText = context.getString(R.string.total_invoices)
//    setCenterTextSize(12f)
//    setEntryLabelTextSize(16f)
//
//    val pieEntries =
//        listOf(PieEntry(payments.toFloat(), "Payments"), PieEntry(dept.toFloat(), "Dept"))
//
//    val pieDatSet = PieDataSet(pieEntries, "Customer Activities")
//
//    pieDatSet.apply {
//        colors = listOf(Color.parseColor("#08C1B0"), Color.parseColor("#D81B60"))
//        valueTextSize = 15f
//        setDrawEntryLabels(true)
//    }
//
//    val pieData = PieData(pieDatSet)
//    data = pieData
//    description = null
//    invalidate()
//}
