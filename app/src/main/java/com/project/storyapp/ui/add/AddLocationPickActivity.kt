package com.project.storyapp.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityAddLocationPickBinding
import com.project.storyapp.databinding.CustomTooltipPickLocationStoryBinding
import com.project.storyapp.utils.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class AddLocationPickActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.InfoWindowAdapter {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityAddLocationPickBinding
    private val viewModel: AddStoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationPickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        onAction()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun onAction() {
        binding.apply {
            btnSelectLocation.setOnClickListener {
                if (viewModel.isLocationPicked.value == true) {
                    Intent().also {
                        it.putExtra(LocationPicker.IsPicked.name, viewModel.isLocationPicked.value)
                        it.putExtra(LocationPicker.Latitude.name, viewModel.latitude.value)
                        it.putExtra(LocationPicker.Longitude.name, viewModel.longitude.value)
                        setResult(RESULT_OK, it)
                        finish()
                    }
                } else
                    showInfoDialog(
                        this@AddLocationPickActivity,
                        getString(R.string.UI_validation_maps_area)
                    ) {
                        it.dismiss()
                    }

                btnCancel.setOnClickListener {
                    viewModel.isLocationPicked.postValue(false)
                    finish()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isTiltGesturesEnabled = true
        }
        mMap.apply {
            moveCamera(CameraUpdateFactory.newLatLngZoom(indonesianLocation, 4f))
            setInfoWindowAdapter(this@AddLocationPickActivity)
            setOnInfoWindowClickListener { marker ->
                postLocationSelected(marker.position.latitude, marker.position.longitude)
                marker.hideInfoWindow()
            }

            setOnMapClickListener {
                clear()
                addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                )?.showInfoWindow()
            }

            setOnPoiClickListener {
                showToast(it.name)
                clear()
                addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                it.latLng.latitude,
                                it.latLng.longitude
                            )
                        )
                )?.showInfoWindow()
            }

            setMapLocationStyle()
            getMyLastLocation()

        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (isPermissionGranted(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && isPermissionGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), 20f
                        )
                    )
                    postLocationSelected(it.latitude, it.longitude)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: false -> getMyLastLocation()
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: false -> getMyLastLocation()
                else -> showToast(getString(R.string.needed_permission))
            }
        }

    private fun setMapLocationStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.gmaps_style))

            if (!success) Timber.e("Style parsing failed")

        } catch (e: Resources.NotFoundException) {
            Timber.e("cannot find style, Error: ${e.message}")
        }
    }

    private fun postLocationSelected(lat: Double, lon: Double) {
        val address = parseAddressLocation(this, lat, lon)
        binding.addressBar.text = address
        viewModel.apply {
            isLocationPicked.postValue(true)
            latitude.postValue(lat)
            longitude.postValue(lon)
        }
    }

    override fun getInfoContents(p0: Marker): View? = null

    override fun getInfoWindow(marker: Marker): View {
        val bindingTooltips =
            CustomTooltipPickLocationStoryBinding.inflate(LayoutInflater.from(this), null, false)

        bindingTooltips.location.text =
            parseAddressLocation(this, marker.position.latitude, marker.position.longitude)

        return bindingTooltips.root
    }
}