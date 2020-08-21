package com.shouman.apps.reseller.admin.domain

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.shouman.apps.reseller.admin.BR
import com.shouman.apps.reseller.admin.api.ServerCustomer
import com.shouman.apps.reseller.admin.api.ServerVisit
import com.shouman.apps.reseller.admin.data.model.SalesmanStatus
import kotlinx.android.parcel.Parcelize

data class DomainBranch(

    val branchName: String,

    val country: String,

    val city: String
)


data class DomainSalesman(

    val salesmanName: String,

    val salesmanPhoneNumber: String,

    val imgURL: String?,

    val salesmanStatus: SalesmanStatus
)


class DomainBranchSalesmen(
    val databaseBranch: DomainBranch,

    val salesmenList: List<DomainSalesman>
)

@Parcelize
data class DomainCustomer(

    val id: Long,

    var createTime: Long,

    val createdBy: String,

    var branchId: Long?,

    private var _customerName: String,

    private var _supervisor: String,

    private var _businessName: String,

    private var _phoneNum: String,

    private var _email: String?,

    var latitude: Double?,

    var longitude: Double?,

    var visit: DomainVisit?
) : Parcelable, BaseObservable() {

    var customerName
        @Bindable get() = _customerName
        set(value) {
            _customerName = value
            notifyPropertyChanged(BR.customerName)
        }

    var businessName
        @Bindable get() = _businessName
        set(value) {
            _businessName = value
            notifyPropertyChanged(BR.businessName)
        }

    var supervisor
        @Bindable get() = _supervisor
        set(value) {
            _supervisor = value
            notifyPropertyChanged(BR.supervisor)
        }

    var phone
        @Bindable get() = _phoneNum
        set(value) {
            _phoneNum = value
            notifyPropertyChanged(BR.phone)
        }

    var email
        @Bindable get() = _email
        set(value) {
            _email = value
            notifyPropertyChanged(BR.email)
        }
}

@Parcelize
data class DomainVisit(

    val id: Long,

    var customerId: Long?,

    var createTime: Long,

    val createdBy: String,

    private var _invoiceNum: Long?,

    private var _invoicePrice: Long?,

    private var _payment: Long?
) : Parcelable, BaseObservable() {

    var invoiceNum
        @Bindable get() = _invoiceNum?.toString()
        set(value) {
            _invoiceNum = try {
                value?.toLong()
            } catch (e: java.lang.NumberFormatException) {
                null
            }
            notifyPropertyChanged(BR.invoiceNum)
        }

    var invoicePrice
        @Bindable get() = _invoicePrice?.toString()
        set(value) {
            _invoicePrice = try {
                value?.toLong()
            } catch (E: NumberFormatException) {
                null
            }
            notifyPropertyChanged(BR.invoicePrice)
        }


    var payment
        @Bindable get() = _payment?.toString()
        set(value) {
            _payment = try {
                value?.toLong()
            } catch (e: NumberFormatException) {
                null
            }
            notifyPropertyChanged(BR.payment)
        }
}

fun DomainCustomer.toServerCustomer(): ServerCustomer {
    return ServerCustomer(
        id,
        createTime,
        createdBy,
        customerName, supervisor, businessName,
        phone, email,
        latitude!!, longitude!!,
        visit!!.toServerVisit()
    )
}

fun DomainVisit.toServerVisit(): ServerVisit {
    return ServerVisit(
        id,
        createTime, createdBy,
        invoiceNum?.toLong(),
        invoicePrice?.toInt() ?: 0,
        payment?.toInt() ?: 0
    )
}


