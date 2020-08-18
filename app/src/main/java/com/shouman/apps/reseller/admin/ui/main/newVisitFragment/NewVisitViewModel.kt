package com.shouman.apps.reseller.admin.ui.main.newVisitFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.data.model.DatabaseCustomer
import com.shouman.apps.reseller.admin.domain.DomainCustomer
import com.shouman.apps.reseller.admin.domain.DomainVisit
import com.shouman.apps.reseller.admin.repository.CustomersRepo

class NewVisitViewModel(application: Application) : AndroidViewModel(application) {


    private val _visitMutableLiveData = MutableLiveData<DomainVisit?>()
    val visitLiveData: LiveData<DomainVisit?>
        get() = _visitMutableLiveData


    private val database = ResellerDatabase.getInstance(application)
    private val customersRepo = CustomersRepo(database)

    val customersList = customersRepo.customersList

    val selectedCustomerId = MutableLiveData<Long?>().apply {
        this.value = -1
    }

    var selectedCustomer:DatabaseCustomer? = null


    val visit = DomainVisit(
        -555,
        null,
        0,
        "Admin",
        null,
        null,
        null
    )

    fun createVisit() {
        visit.customerId = selectedCustomerId.value
        visit.createTime = System.currentTimeMillis()
        _visitMutableLiveData.value = visit
    }

    fun restoreVisitState() {
        _visitMutableLiveData.value = null
    }
}