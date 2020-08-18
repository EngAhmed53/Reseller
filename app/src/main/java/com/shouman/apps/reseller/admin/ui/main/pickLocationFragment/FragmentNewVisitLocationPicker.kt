package com.shouman.apps.reseller.admin.ui.main.pickLocationFragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.databinding.FragmentNewVisitLocationPickerBinding


class FragmentNewVisitLocationPicker : Fragment(), OnMapReadyCallback {

    private val LOCATION_REQUEST = 225
    private val MY_PERMISSIONS_ACCESS_FINE_LOCATION = 220

    private lateinit var viewModel: PickLocationViewModel
    private lateinit var uploadVisitViewModel: UploadVisitViewModel
    private lateinit var mBinding: FragmentNewVisitLocationPickerBinding

    private var googleMap: GoogleMap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentNewVisitLocationPickerBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(PickLocationViewModel::class.java)
        uploadVisitViewModel = ViewModelProvider(this).get(UploadVisitViewModel::class.java)

        setupMapView(savedInstanceState)

        mBinding.lifecycleOwner = this
        mBinding.locationViewModel = viewModel
        mBinding.uploadVisitViewModel = uploadVisitViewModel
        mBinding.visit = FragmentNewVisitLocationPickerArgs.fromBundle(requireArguments()).visit

        uploadVisitViewModel.customerLatLng =
            FragmentNewVisitLocationPickerArgs.fromBundle(requireArguments()).customerLatLng


        uploadVisitViewModel.uploadVisitStatus.observe(viewLifecycleOwner, Observer { status ->

            when (status) {
                AddNewVisitStatus.SUCCESS -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.saved_successfully),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    uploadVisitViewModel.restoreUploadStatus()
                    //back to home fragment
                    findNavController().popBackStack(R.id.transparentFragment, false)
                }
                AddNewVisitStatus.LOCATIONS_NOT_IDENTICAL -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.location_not_identical),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    uploadVisitViewModel.restoreUploadStatus()
                }
                AddNewVisitStatus.CUSTOMER_ID_NOT_VALID -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.customer_id_not_valid),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    uploadVisitViewModel.restoreUploadStatus()
                }

                AddNewVisitStatus.TIMED_OUT -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.connection_time_out),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    uploadVisitViewModel.restoreUploadStatus()
                }
                AddNewVisitStatus.ERROR -> {
                    val contextView = mBinding.root
                    Snackbar.make(
                        contextView,
                        getString(R.string.no_internet_connection),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    uploadVisitViewModel.restoreUploadStatus()
                }
                else -> return@Observer
            }
        })

        return mBinding.root
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map

        viewModel.permissionState.observe(viewLifecycleOwner, Observer {
            if (it == false) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION
                )
            } else if (it == true) {
                googleMap?.let {
                    checkLocationProviders()
                    setMapUI()
                }
            }
        })

        viewModel.latLng.observe(viewLifecycleOwner, Observer { latLng ->
            latLng?.let {
                showCurrentLocationOnMap(latLng)
                showSavedLocationOnMap(uploadVisitViewModel.customerLatLng!!)
                uploadVisitViewModel.fetchedLatLng = latLng
                viewModel.restoreLatLngState()
            }
        })
    }

    private fun setMapUI() {
        googleMap!!.uiSettings.setAllGesturesEnabled(true)
        googleMap!!.uiSettings.isMapToolbarEnabled = true
        googleMap!!.uiSettings.isMyLocationButtonEnabled = true
        googleMap!!.uiSettings.isCompassEnabled = true
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap!!.isMyLocationEnabled = true
        mBinding.map.findViewWithTag<View>("GoogleMapMyLocationButton").visibility = View.GONE
        googleMap!!.isBuildingsEnabled = true
    }

    fun showSavedLocationOnMap(latLng: LatLng) {
        googleMap?.apply {
            addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.customer_location))
            )
        }
    }

    private fun showCurrentLocationOnMap(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
        googleMap!!.moveCamera(cameraUpdate)
    }

    private fun checkLocationProviders() {
        if (!isLocationEnabled()) {
            println("location provider not enabled")
            Toast.makeText(requireContext(), R.string.turn_on_location, Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            if (intent.resolveActivity(requireActivity().packageManager) != null) startActivityForResult(
                intent,
                LOCATION_REQUEST
            )
        } else {
            println("updating location from checkLocationProvider")
            viewModel.updateLocation()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Toast.makeText(requireContext(), R.string.location_permission, Toast.LENGTH_SHORT)
            .show()
        if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                println("permission granted")
                checkLocationProviders()
                viewModel.restorePermissionState()
            } else {
                println("permission not granted")
                Toast.makeText(
                    requireContext(),
                    R.string.location_permission_needed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun setupMapView(savedInstanceState: Bundle?) {
        mBinding.map.getMapAsync(this)
        mBinding.map.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mBinding.map.onStart()
    }

    override fun onResume() {
        super.onResume()
        mBinding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.map.onPause()
    }

    override fun onStop() {
        super.onStop()
        mBinding.map.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.map.onLowMemory()
    }
}