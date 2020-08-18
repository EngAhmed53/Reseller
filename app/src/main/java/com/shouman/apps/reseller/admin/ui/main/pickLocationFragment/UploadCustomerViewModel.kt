package com.shouman.apps.reseller.admin.ui.main.pickLocationFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.ResponseCode
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.domain.DomainCustomer
import com.shouman.apps.reseller.admin.domain.toServerCustomer
import com.shouman.apps.reseller.admin.repository.CustomersRepo
import kotlinx.coroutines.*


enum class AddNewCustomerStatus {
    STARTED,
    SUCCESS,
    TIMED_OUT,
    COMPANY_ID_NOT_VALID,
    BRANCH_ID_NOT_VALID,
    ERROR
}


class UploadCustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val TIMER = 10000L

    private val _uploadCustomerStatus = MutableLiveData<AddNewCustomerStatus?>()
    val uploadCustomerStatus: LiveData<AddNewCustomerStatus?>
        get() = _uploadCustomerStatus

    val isUploadStatusStarted = Transformations.map(_uploadCustomerStatus) {
        it == AddNewCustomerStatus.STARTED
    }

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    private val database = ResellerDatabase.getInstance(application)
    private val customersRepo = CustomersRepo(database)

    var fetchedLatLng: LatLng? = null


    fun sendCustomerToServer(customer: DomainCustomer) {
        customer.latitude = fetchedLatLng?.latitude
        customer.longitude = fetchedLatLng?.longitude

        viewModelScope.launch {
            val job = withTimeoutOrNull(TIMER) {
                try {
                    uploadCustomer(customer)
                } catch (e: Exception) {
                    Log.e("error", "server exception", e)
                    _uploadCustomerStatus.postValue(AddNewCustomerStatus.ERROR)
                }
            }
            if (job == null) _uploadCustomerStatus.postValue(AddNewCustomerStatus.TIMED_OUT)
        }
    }

    private suspend fun uploadCustomer(customer: DomainCustomer) {
        _uploadCustomerStatus.postValue(AddNewCustomerStatus.STARTED)
        val customersService = NetworkCall.customersServices
        val response = customersService.addNewCustomerByAdminAsync(
            3,
            customer.branchId!!,
            customer.toServerCustomer()
        ).await()

        when (response.responseCode) {
            ResponseCode.SUCCESS -> {
                val databaseCustomer = response.body
                databaseCustomer?.let {
                    customersRepo.addNewCustomer(it)
                    _uploadCustomerStatus.postValue(AddNewCustomerStatus.SUCCESS)
                }
            }

            ResponseCode.COMPANY_ID_NOT_VALID -> {
                _uploadCustomerStatus.postValue(AddNewCustomerStatus.COMPANY_ID_NOT_VALID)
            }
            ResponseCode.BRANCH_ID_NOT_VALID -> {
                _uploadCustomerStatus.postValue(AddNewCustomerStatus.BRANCH_ID_NOT_VALID)
            }
            else -> {
                _uploadCustomerStatus.postValue(AddNewCustomerStatus.ERROR)
            }
        }
    }

    fun restoreUploadStatus() {
        _uploadCustomerStatus.value = null
    }
}

