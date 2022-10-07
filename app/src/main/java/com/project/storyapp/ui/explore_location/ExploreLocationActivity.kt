package com.project.storyapp.ui.explore_location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.project.core.data.source.Resource
import com.project.core.domain.model.Story
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityExploreLocationBinding
import com.project.storyapp.databinding.CustomTooltipMapsExploreBinding
import com.project.storyapp.ui.detail.DetailActivity
import com.project.storyapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.StringBuilder

@Suppress("CAST_NEVER_SUCCEEDS")
class ExploreLocationActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.InfoWindowAdapter {

    private lateinit var binding: ActivityExploreLocationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var loading: AlertDialog

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: ExploreLocationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_explore) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isTiltGesturesEnabled = true
        }

        scope.launch {
            val token = "Bearer ${viewModel.getUserToken()}"

            withContext(Dispatchers.Main) {
                viewModel.getStoriesWithLocation(token, 100).observe(this@ExploreLocationActivity) {
                    when (it) {
                        is Resource.Loading -> loading.show()
                        is Resource.Success -> {
                            loading.dismiss()
                            val data = it.data
                            if (data?.isNotEmpty() == true) {
                                for (story in data) {
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(
                                                LatLng(
                                                    story.lat?.toDouble() ?: 0.0,
                                                    story.lon?.toDouble() ?: 0.0
                                                )
                                            )
                                    )?.tag = story
                                }

                                mMap.apply {
                                    setInfoWindowAdapter(this@ExploreLocationActivity)
                                    setOnInfoWindowClickListener { marker ->
                                        val dataStory = marker.tag as Story

                                        routeToDetailStory(dataStory)
                                    }
                                }

                                getMyLocation()
                                setMapLocationStyle()

                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        indonesianLocation,
                                        4f
                                    )
                                )
                            } else {
                                showInfoDialog(
                                    this@ExploreLocationActivity,
                                    getString(R.string.location_not_found)
                                ) { dialog ->
                                    dialog.dismiss()
                                    finish()
                                }
                            }
                        }
                        is Resource.Error -> {
                            loading.dismiss()
                            showToast(it.message.toString())
                        }
                    }
                }
            }
        }

    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.coordinateTemp.postValue(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else {
                    viewModel.coordinateTemp.postValue(indonesianLocation)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted)
                getMyLocation()
            else
                showToast(getString(R.string.needed_permission))
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

    private fun routeToDetailStory(data: Story) {
        Intent(this, DetailActivity::class.java).also { intent ->
            intent.putExtra(DetailActivity.DATA_STORY, data)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun getInfoContents(p0: Marker): View? = null

    override fun getInfoWindow(marker: Marker): View {
        val bindingTooltips =
            CustomTooltipMapsExploreBinding.inflate(LayoutInflater.from(this))
        val data: Story = marker.tag as Story
        bindingTooltips.labelLocation.text = parseAddressLocation(
            this@ExploreLocationActivity,
            marker.position.latitude, marker.position.longitude
        )
        bindingTooltips.apply {
            name.text = StringBuilder("Story by ").append(data.name)
            image.setImageBitmap(
                bitmapFromURL(
                    this@ExploreLocationActivity,
                    data.photoUrl
                )
            )
            storyDescription.text = data.description
            storyUploadTime.text =
                getTimeLineUploaded(this@ExploreLocationActivity, data.createdAt)
        }
        return bindingTooltips.root
    }
}