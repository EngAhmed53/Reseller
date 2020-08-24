package com.shouman.apps.reseller.admin.repository.paging.companyActivities

import android.app.Application
import androidx.paging.DataSource
import com.shouman.apps.reseller.admin.api.PageableActivity
import kotlinx.coroutines.CoroutineScope

class ActivitiesDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    val application: Application,
    var date: Long
) : DataSource.Factory<Int, PageableActivity>() {

    override fun create(): DataSource<Int, PageableActivity> {
        return ActivitiesDataSource(coroutineScope, application, date)
    }
}