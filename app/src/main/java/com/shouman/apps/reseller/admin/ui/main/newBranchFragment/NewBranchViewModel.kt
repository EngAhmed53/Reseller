package com.shouman.apps.reseller.admin.ui.main.newBranchFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.shouman.apps.reseller.admin.api.ResponseCode
import com.shouman.apps.reseller.admin.api.ServerBranch
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.repository.BranchesRepository
import kotlinx.coroutines.*
import java.lang.Exception


enum class NewBranchStatus {
    STARTED,
    COMPLETED,
    TIMED_OUT,
    ERROR
}


class NewBranchViewModel(application: Application):AndroidViewModel(application) {
    private val TIMER: Long = 10000

    private val _newBranchStatus = MediatorLiveData<NewBranchStatus?>()
    val newBranchStatus: LiveData<NewBranchStatus?>
        get() = _newBranchStatus


    val isProgressVisible: LiveData<Boolean> = Transformations.map(_newBranchStatus) {
        it == NewBranchStatus.STARTED
    }

    private val viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val database = ResellerDatabase.getInstance(application)
    private val branchesRepository = BranchesRepository(database)

    fun newBranch(branchName: String) {
        try {
            addNewBranch(branchName)
        } catch (e: Exception) {
            _newBranchStatus.value = NewBranchStatus.ERROR
        }
    }

    fun addNewBranch(branchName: String) {
        coroutineScope.launch {

            _newBranchStatus.postValue(NewBranchStatus.STARTED)

            val job = withTimeoutOrNull(TIMER) {
                val responseCode = branchesRepository.addNewBranchToCompany(
                    getApplication(),
                    ServerBranch(
                        -555, branchName, "Egypt", "Cairo", HashSet()
                    )
                )

                when (responseCode) {
                    ResponseCode.SUCCESS -> _newBranchStatus.postValue(NewBranchStatus.COMPLETED)
                    else -> _newBranchStatus.postValue(NewBranchStatus.ERROR)
                }
            }
            if (job == null) _newBranchStatus.postValue(NewBranchStatus.TIMED_OUT)
        }
    }

    fun restoreBranchStatus() {
        _newBranchStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}