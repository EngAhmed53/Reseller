package com.shouman.apps.reseller.admin.ui.main.customerProfileFragment

import android.app.Application
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.shouman.apps.reseller.admin.api.CustomerProfile
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.ResponseCode
import kotlinx.coroutines.*
import java.util.*


enum class GetCustomerProfileStatus {
    STARTED,
    SUCCESS,
    TIMED_OUT,
    COMPANY_ID_NOT_VALID,
    CUSTOMER_ID_NOT_VALID,
    ERROR
}

@Suppress("BlockingMethodInNonBlockingContext")
class CustomerProfileViewModel(val application: Application, private val customerId: Long) :
    ViewModel() {

    private val TIMER: Long = 10000
    private val _customerProfileLiveData = MutableLiveData<CustomerProfile?>()
    val customerProfileLiveData: LiveData<CustomerProfile?>
        get() = _customerProfileLiveData

    private val _getCustomerProfileStatus = MutableLiveData<GetCustomerProfileStatus?>()
    val getCustomerProfileStatus: LiveData<GetCustomerProfileStatus?>
        get() = _getCustomerProfileStatus

    val isProfileFetchedStarted = Transformations.map(_getCustomerProfileStatus) {
        it == GetCustomerProfileStatus.STARTED
    }

    val isProfileFetchedCompleted = Transformations.map(_getCustomerProfileStatus) {
        it == GetCustomerProfileStatus.SUCCESS
    }

    val isConnectionErrorOccurred = Transformations.map(_getCustomerProfileStatus) {
        it == GetCustomerProfileStatus.ERROR || it == GetCustomerProfileStatus.TIMED_OUT
    }

    val isIdentityErrorOccurred = Transformations.map(_getCustomerProfileStatus) {
        it == GetCustomerProfileStatus.COMPANY_ID_NOT_VALID || it == GetCustomerProfileStatus.CUSTOMER_ID_NOT_VALID
    }

    private val _customerAddress = MutableLiveData<String>()
    val customerAddress: LiveData<String>
        get() = _customerAddress

    private val _makePhoneCallIntent = MutableLiveData<Intent?>()
    val makePhoneCallIntent: LiveData<Intent?>
        get() = _makePhoneCallIntent

    private val _openLocationOnMap = MutableLiveData<Intent?>()
    val openLocationOnMap: LiveData<Intent?>
        get() = _openLocationOnMap

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        getCustomerProfile()
    }

    private fun getCustomerProfile() {
        viewModelScope.launch {
            val job = withTimeoutOrNull(TIMER) {

                _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.STARTED)

                try {
                    val companyId = 3L //Get the Company Id from the Preferences using [application]
                    getCustomerProfileFromServer(companyId)
                } catch (e: Exception) {
                    Log.e("Error", "gettingCustomerProfile", e)
                    _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.ERROR)
                }
            }
            if (job == null) _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.TIMED_OUT)
        }
    }

    private suspend fun getCustomerProfileFromServer(companyId: Long) {
        val customersServices = NetworkCall.customersServices

        val response =
            customersServices.getCustomerProfileAsync(companyId, customerId).await()

        when (response.responseCode) {
            ResponseCode.SUCCESS -> {
                _customerProfileLiveData.postValue(response.body)
                _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.SUCCESS)
                getAddress(response.body!!.latitude, response.body.longitude)
            }
            ResponseCode.COMPANY_ID_NOT_VALID -> {
                _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.COMPANY_ID_NOT_VALID)
            }
            ResponseCode.CUSTOMER_ID_NOT_VALID -> {
                _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.CUSTOMER_ID_NOT_VALID)
            }
            else -> {
                _getCustomerProfileStatus.postValue(GetCustomerProfileStatus.ERROR)
            }
        }
    }


    fun makeCall(phoneNumber: String?) {
        phoneNumber?.let {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            _makePhoneCallIntent.value = intent
        }
    }

    fun openLocationOnMap(latitude: Double?, longitude: Double?) {
        if (latitude == null || longitude == null) return
        val intentUri = Uri.parse(
            "geo:0,0?q=$latitude, $longitude"
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        _openLocationOnMap.value = mapIntent
    }

    fun getAddress(latitude: Double?, longitude: Double?) {
        if (latitude == null || longitude == null) return

//            val city: String = addresses[0].getLocality()
//            val state: String = addresses[0].getAdminArea()
//            val country: String = addresses[0].getCountryName()
//            val postalCode: String = addresses[0].getPostalCode()
//            val knownName: String = addresses[0].getFeatureName()
//        return try {
//            var address = ""
//            viewModelScope.launch {
//                address = getAddress(latitude, longitude)
//            }
//            address
//        } catch (e: Exception) {
//            "($latitude, $longitude)"
//        }

        viewModelScope.launch {
            val geoCoder = Geocoder(application, Locale.getDefault())
            try {
                val list = getAddressList(geoCoder, latitude, longitude)
                println("size  = ${list[0].getAddressLine(0)}")
                _customerAddress.postValue("${list[0].getAddressLine(0)}, ${list[0].adminArea}")
            } catch (e: java.lang.Exception) {
                _customerAddress.postValue("($latitude, $longitude)")
            }
        }
    }

    private suspend fun getAddressList(
        geoCoder: Geocoder,
        latitude: Double,
        longitude: Double
    ): List<Address> {
        val addresses: List<Address>
        withContext(Dispatchers.IO) {
            addresses = geoCoder.getFromLocation(
                latitude, longitude, 1
            )
        }
        return addresses
    }

    fun restoreCustomerProfileStatus() {
        _getCustomerProfileStatus.value = null
    }

    fun restorePhoneCallIntent() {
        _makePhoneCallIntent.value = null
    }

    fun restoreMapIntent() {
        _openLocationOnMap.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}


@Suppress("UNCHECKED_CAST")
class CustomerProfileViewModelFactory(
    private val application: Application,
    private val customerId: Long
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerProfileViewModel::class.java)) {
            return CustomerProfileViewModel(application, customerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}