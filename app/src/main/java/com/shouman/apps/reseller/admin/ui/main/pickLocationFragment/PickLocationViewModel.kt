package com.shouman.apps.reseller.admin.ui.main.pickLocationFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng


class PickLocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _latLng = MutableLiveData<LatLng?>()
    val latLng: LiveData<LatLng?>
        get() = _latLng

    private val _isLocationReady = MutableLiveData<Boolean?>()
    val isLocationReady: LiveData<Boolean?>
        get() = _isLocationReady


    private val _permissionState = MutableLiveData<Boolean>()
    val permissionState: LiveData<Boolean>
        get() = _permissionState


    private val fusedLocation: FusedLocationProviderClient? =
        LocationServices.getFusedLocationProviderClient(application)

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.let {
                val location = result.lastLocation
                val latLong = LatLng(location.latitude, location.longitude)
                println("location fetched")
                _latLng.value = latLong
                _isLocationReady.value = true
            }
        }
    }

    init {

        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _permissionState.value = false

        } else {
            _permissionState.value = true
            updateLocation()
        }
    }


    @SuppressLint("MissingPermission")
    fun updateLocation() {
        _isLocationReady.value = false

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocation?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    fun restorePermissionState() {
        _permissionState.value = null
    }

    fun restoreLatLngState() {
        _latLng.value = null
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocation?.removeLocationUpdates(mLocationCallback)
    }
}