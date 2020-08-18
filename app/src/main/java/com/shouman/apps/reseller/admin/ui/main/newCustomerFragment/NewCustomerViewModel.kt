package com.shouman.apps.reseller.admin.ui.main.newCustomerFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.domain.DomainCustomer
import com.shouman.apps.reseller.admin.domain.DomainVisit
import com.shouman.apps.reseller.admin.repository.BranchesRepository

class NewCustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val _customerMutableLiveData = MutableLiveData<DomainCustomer?>()
    val customerMutableLiveData: LiveData<DomainCustomer?>
        get() = _customerMutableLiveData


    private val database = ResellerDatabase.getInstance(application)
    private val branchesRepository = BranchesRepository(database)

    val miniBranchesList = branchesRepository.miniDatabaseBranchesList

    val selectedBranchId = MutableLiveData<Long?>().apply {
        this.value = -1
    }


    val customer = DomainCustomer(
        -555,
        0,
        "Admin",
        null,
        "",
        "",
        "",
        "",
        "",
        null,
        null,
        null
    )

    val visit = DomainVisit(
        -555,
        null,
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
        customer.branchId = selectedBranchId.value
        customer.visit = visit

        _customerMutableLiveData.value = customer
    }

    fun restoreCustomerObject() {
        _customerMutableLiveData.value = null
    }

}