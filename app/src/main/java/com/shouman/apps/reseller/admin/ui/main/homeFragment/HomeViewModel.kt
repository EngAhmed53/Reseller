package com.shouman.apps.reseller.admin.ui.main.homeFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.shouman.apps.reseller.admin.api.CompanyDateSummary
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.PageableActivity
import com.shouman.apps.reseller.admin.api.ResponseCode
import com.shouman.apps.reseller.admin.common.getCurrentDateWithoutTime
import com.shouman.apps.reseller.admin.repository.paging.companyActivities.ActivitiesDataSourceFactory
import com.shouman.apps.reseller.admin.repository.paging.companyActivities.DataStatusObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

enum class DateSummaryStatus {
    STARTED,
    SUCCESS,
    ERROR
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val _daySummaryLiveData = MutableLiveData<CompanyDateSummary?>()
    val daySummaryLiveData: LiveData<CompanyDateSummary?>
        get() = _daySummaryLiveData

    private val _daySummaryStatusLiveData = MutableLiveData<DateSummaryStatus?>()
    val daySummaryStatusLiveData: LiveData<DateSummaryStatus?>
        get() = _daySummaryStatusLiveData

    private val _selectedDayLiveData = MutableLiveData<Long?>()
    val selectedDayLiveData: LiveData<Long?>
        get() = _selectedDayLiveData

    val isDataLoadingStarted = Transformations.map(_daySummaryStatusLiveData) {
        it == DateSummaryStatus.STARTED
    }

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    val date: Long = getCurrentDateWithoutTime().time

    init {
        _selectedDayLiveData.value = date
        getSelectedDateSummary(date)
    }

    fun getSelectedDateSummary(date: Long) {
        viewModelScope.launch {

            try {
                getDateSummary(date)
            } catch (e: Exception) {
                _daySummaryStatusLiveData.postValue(DateSummaryStatus.ERROR)
            }
        }
    }

    private suspend fun getDateSummary(date: Long) {
        _daySummaryStatusLiveData.postValue(DateSummaryStatus.STARTED)
        val activitiesApiServices = NetworkCall.activitiesServices
        val companyId = 3L //should be changed to use userPreference to get companyId
        val response = activitiesApiServices.getCompanyDaySummaryAsync(companyId, date).await()

        when (response.responseCode) {
            ResponseCode.SUCCESS -> {
                _daySummaryLiveData.postValue(response.body)
                _selectedDayLiveData.postValue(date)
                getDateActivities(date)
            }
            else -> {
                _daySummaryStatusLiveData.postValue(DateSummaryStatus.ERROR)
            }
        }
    }

    private var factory =
        ActivitiesDataSourceFactory(viewModelScope, application, date)

    var dataStatus = DataStatusObject.dataStat

    private val pagedListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(5)
            .setPageSize(5).setPrefetchDistance(5).build()


    val pagedLiveData =
        LivePagedListBuilder<Int, PageableActivity>(factory, pagedListConfig).build()


    private fun getDateActivities(newDate: Long) {
        factory.date = newDate
        pagedLiveData.value?.dataSource?.invalidate()
    }

    fun restoreDaySummaryStatus() {
        _daySummaryStatusLiveData.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}