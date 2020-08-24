package com.shouman.apps.reseller.admin.repository.paging.companyCustomers

import android.app.Application
import androidx.paging.DataSource
import com.shouman.apps.reseller.admin.api.PageableCustomer
import kotlinx.coroutines.CoroutineScope

class CustomersDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    val application: Application
) : DataSource.Factory<Int, PageableCustomer>() {


    override fun create(): DataSource<Int, PageableCustomer> {
        return CustomerDataSource(coroutineScope, application)
    }
}