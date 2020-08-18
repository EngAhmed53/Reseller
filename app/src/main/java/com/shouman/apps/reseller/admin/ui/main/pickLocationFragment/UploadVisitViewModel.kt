package com.shouman.apps.reseller.admin.ui.main.pickLocationFragment

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.ResponseCode
import com.shouman.apps.reseller.admin.api.ServerVisit
import com.shouman.apps.reseller.admin.domain.DomainVisit
import kotlinx.coroutines.*


enum class AddNewVisitStatus {
    STARTED,
    SUCCESS,
    TIMED_OUT,
    CUSTOMER_ID_NOT_VALID,
    LOCATIONS_NOT_IDENTICAL,
    ERROR
}

private const val MAXIMUM_DISTANCE: Float = 800f
private const val MINIMUM_DISTANCE: Float = 0f

class UploadVisitViewModel(application: Application) : AndroidViewModel(application) {

    val context = application

    private val TIMER = 10000L

    private val _uploadVisitStatus = MutableLiveData<AddNewVisitStatus?>()
    val uploadVisitStatus: LiveData<AddNewVisitStatus?>
        get() = _uploadVisitStatus

    val isUploadStatusStarted = Transformations.map(_uploadVisitStatus) {
        it == AddNewVisitStatus.STARTED
    }

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    var fetchedLatLng: LatLng? = null
    var customerLatLng: LatLng? = null


    fun sendVisitToServer(visit: DomainVisit) {
        viewModelScope.launch {
            val job = withTimeoutOrNull(TIMER) {
                try {
                    if (isLocationsIdentical()) {
                        uploadVisit(visit)
                    } else _uploadVisitStatus.postValue(AddNewVisitStatus.LOCATIONS_NOT_IDENTICAL)
                } catch (e: Exception) {
                    Log.e("error", "server exception", e)
                    _uploadVisitStatus.postValue(AddNewVisitStatus.ERROR)
                }
            }
            if (job == null) _uploadVisitStatus.postValue(AddNewVisitStatus.TIMED_OUT)
        }
    }

    private suspend fun uploadVisit(visit: DomainVisit) {
        _uploadVisitStatus.postValue(AddNewVisitStatus.STARTED)
        val visitsApiServices = NetworkCall.visitsServices
        val response = visitsApiServices.addNewVisitByAdminAsync(
            3,
            visit.customerId!!,
            ServerVisit(
                visit.id,
                visit.createTime,
                visit.createdBy,
                visit.invoiceNum?.toLong(),
                visit.invoicePrice?.toInt() ?: 0,
                visit.payment?.toInt() ?: 0
            )
        ).await()

        when (response) {
            ResponseCode.SUCCESS -> {
                _uploadVisitStatus.postValue(AddNewVisitStatus.SUCCESS)
            }

            ResponseCode.CUSTOMER_ID_NOT_VALID -> {
                _uploadVisitStatus.postValue(AddNewVisitStatus.CUSTOMER_ID_NOT_VALID)
            }

            else -> {
                _uploadVisitStatus.postValue(AddNewVisitStatus.ERROR)
            }
        }
    }

    private fun isLocationsIdentical(): Boolean {
        //get the difference between the current sales location and the customer location
        val fetchedLocation =
            Location(context.getString(R.string.app_name))
        val savedLocation =
            Location(context.getString(R.string.app_name))
        fetchedLocation.latitude = fetchedLatLng!!.latitude
        fetchedLocation.longitude = fetchedLatLng!!.longitude
        savedLocation.latitude = customerLatLng!!.latitude
        savedLocation.longitude = customerLatLng!!.longitude

        println(fetchedLatLng)
        println(customerLatLng)

        //the difference distance in meters
        val distance: Float = fetchedLocation.distanceTo(savedLocation)

        println(distance)

        return distance in MINIMUM_DISTANCE..MAXIMUM_DISTANCE
    }

    fun restoreUploadStatus() {
        _uploadVisitStatus.value = null
    }
}

