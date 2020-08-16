package com.shouman.apps.reseller.admin.ui.main.customersFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.shouman.apps.reseller.admin.api.PageableCustomer
import com.shouman.apps.reseller.admin.repository.paging.CustomersDataSourceFactory
import com.shouman.apps.reseller.admin.repository.paging.DataStatusObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


class CustomersViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val factory = CustomersDataSourceFactory(viewModelScope, application)

    var dataStatus = DataStatusObject.dataStat


    private val pagedListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(10)
            .setPageSize(10).setPrefetchDistance(5).build()


    val pagedLiveData =
        LivePagedListBuilder<Int, PageableCustomer>(factory, pagedListConfig).build()


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}