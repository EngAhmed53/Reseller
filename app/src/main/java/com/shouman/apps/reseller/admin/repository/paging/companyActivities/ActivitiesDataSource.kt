package com.shouman.apps.reseller.admin.repository.paging.companyActivities

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.shouman.apps.reseller.admin.api.ActivitiesApiServices
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.PageableActivity
import com.shouman.apps.reseller.admin.repository.paging.companyCustomers.DataStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


object DataStatusObject {

    val dataStat = MutableLiveData<DataStatus?>()
}

class ActivitiesDataSource(
    private val coroutineScope: CoroutineScope,
    val application: Application,
    val date: Long
) : PageKeyedDataSource<Int, PageableActivity>() {


    private lateinit var activitiesApiServices: ActivitiesApiServices

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PageableActivity>
    ) {
        activitiesApiServices = NetworkCall.activitiesServices

        coroutineScope.launch {

            try {
                DataStatusObject.dataStat.postValue(DataStatus.FETCHING)
                val activitiesList: List<PageableActivity> =
                    activitiesApiServices.getAllCompanyActivitiesByDateAsync(
                        3,
                        date,
                        0,
                        5
                    ).await()

                callback.onResult(activitiesList, null, 1)
                DataStatusObject.dataStat.postValue(DataStatus.FETCHED)
            } catch (e: Exception) {
                Log.e("ActivityDataSource", "failed to load data", e)
                DataStatusObject.dataStat.postValue(DataStatus.ERROR)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PageableActivity>) {
        coroutineScope.launch {
            try {
                DataStatusObject.dataStat.postValue(DataStatus.FETCHING)

                val activitiesList: List<PageableActivity> =
                    activitiesApiServices.getAllCompanyActivitiesByDateAsync(
                        3,
                        date,
                        params.key,
                        5
                    ).await()

                callback.onResult(activitiesList, params.key + 1)
                DataStatusObject.dataStat.postValue(DataStatus.FETCHED)
            } catch (e: Exception) {
                Log.e("ActivityDataSource", "failed to load data", e)
                DataStatusObject.dataStat.postValue(DataStatus.ERROR)
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PageableActivity>
    ) {
    }
}