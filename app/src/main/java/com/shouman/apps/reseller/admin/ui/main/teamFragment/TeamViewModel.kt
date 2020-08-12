package com.shouman.apps.reseller.admin.ui.main.teamFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.repository.BranchesRepository
import kotlinx.coroutines.*


enum class DataStatus {
    LOCAL_EMPTY,
    SERVER_EMPTY,
    FETCHED,
    ERROR
}


class TeamViewModel(application: Application) : AndroidViewModel(application) {

    private val TIMER: Long = 10000
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val database = ResellerDatabase.getInstance(application)
    private val branchesRepository = BranchesRepository(database)


    private val _dataStat = MutableLiveData<DataStatus?>()
    val dataStat: LiveData<DataStatus?>
        get() = _dataStat


    val branchesAndSalesmen = branchesRepository.branchesSalesmenList.apply {
        if (this.value.isNullOrEmpty()) {

            _dataStat.postValue(DataStatus.LOCAL_EMPTY)

            viewModelScope.launch {

                val job = withTimeoutOrNull(TIMER) {
                    fetchDataFromServer(application)
                }
                if (job == null) _dataStat.postValue(DataStatus.ERROR)
            }
        } else _dataStat.postValue(DataStatus.FETCHED)
    }

    private suspend fun fetchDataFromServer(application: Application) {
        try {
            when(branchesRepository.refreshBranchesSalesmenList(application)) {
                true -> _dataStat.postValue(DataStatus.FETCHED)
                false -> _dataStat.postValue(DataStatus.SERVER_EMPTY)
            }
        } catch (e: Exception) {
            Log.e("fitch data","error", e)
            _dataStat.postValue(DataStatus.ERROR)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}