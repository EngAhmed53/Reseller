package com.shouman.apps.reseller.admin.repository.paging

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.shouman.apps.reseller.admin.api.CustomersApiServices
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.PageableCustomer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


enum class DataStatus {
    FETCHING,
    FETCHED,
    ERROR
}

object DataStatusObject {

    val dataStat = MutableLiveData<DataStatus?>()
}
class CustomerDataSource(
    private val coroutineScope: CoroutineScope,
    val application: Application
) : PageKeyedDataSource<Int, PageableCustomer>() {


    private lateinit var customersApiServices: CustomersApiServices

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PageableCustomer>
    ) {
        customersApiServices = NetworkCall.customersServices

        coroutineScope.launch {

            try {
                DataStatusObject.dataStat.postValue(DataStatus.FETCHING)
                val customersList: List<PageableCustomer> =
                    customersApiServices.getAllCustomersAsync(
                        3,
                        0,
                        10
                    ).await()

                callback.onResult(customersList, null, 1)
                DataStatusObject.dataStat.postValue(DataStatus.FETCHED)
            } catch (e: Exception) {
                Log.e("CustomerDataSource", "failed to load data", e)
                DataStatusObject.dataStat.postValue(DataStatus.ERROR)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PageableCustomer>) {
        coroutineScope.launch {
            try {
                DataStatusObject.dataStat.postValue(DataStatus.FETCHED)
                val customersList: List<PageableCustomer> =
                    customersApiServices.getAllCustomersAsync(
                        3,
                        params.key,
                        10
                    ).await()

                callback.onResult(customersList, params.key + 1)
                DataStatusObject.dataStat.postValue(DataStatus.FETCHED)
            } catch (e: Exception) {
                Log.e("CustomerDataSource", "failed to load data", e)
                DataStatusObject.dataStat.postValue(DataStatus.ERROR)
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PageableCustomer>
    ) {
    }
}