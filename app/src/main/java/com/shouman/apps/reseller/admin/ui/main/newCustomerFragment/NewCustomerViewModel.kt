package com.shouman.apps.reseller.admin.ui.main.newCustomerFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shouman.apps.reseller.admin.domain.DomainCustomer
import com.shouman.apps.reseller.admin.domain.DomainVisit

class NewCustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val _customerMutableLiveData = MutableLiveData<DomainCustomer?>()
    val customerMutableLiveData: LiveData<DomainCustomer?>
        get() = _customerMutableLiveData


    val customer = DomainCustomer(
        -555,
        0,
        "Admin",
        "",
        "",
        "",
        "",
        "",
        null,
        null,
        HashSet()
    )

    val visit = DomainVisit(
        -555,
        0,
        "Admin",
        null,
        null,
        null
    )


    fun createCustomer(
    ) {
        visit.createTime = System.currentTimeMillis()
        customer.createTime = System.currentTimeMillis()
        customer.visitsSet.add(visit)

        _customerMutableLiveData.value = customer
    }

    fun restoreCustomerObject() {
        _customerMutableLiveData.value = null
    }

}