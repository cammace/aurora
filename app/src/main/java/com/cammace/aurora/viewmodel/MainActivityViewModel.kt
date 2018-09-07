package com.cammace.aurora.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cammace.aurora.BuildConfig
import com.cammace.aurora.api.DarkskyService
import com.cammace.aurora.model.DarkskyModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception

const val DEFAULT_LOCATION_NAME = "New York City"
@JvmField
val NEW_YORK_LOCATION = Location("default").apply {
  latitude = 40.6973
  longitude = -74.2195
}

class MainActivityViewModel(application: Application) : AndroidViewModel(application),
  OnSuccessListener<Location>, OnFailureListener, Callback<GeocodingResponse> {

  // LiveData
  val requestLocationPermissionLiveData = MutableLiveData<Boolean>()
  val darkSkyApiResponseLiveData = MutableLiveData<DarkskyModel.Darksky>()
  val locationNameLiveData = MutableLiveData<String>()

  // Create the Retrofit instance
  private val forecastAdi by lazy { DarkskyService.create() }

  private var userLocation: Location = NEW_YORK_LOCATION
  private val appContext = getApplication() as Context

  private fun fetchForecastAtUserLocation() {
    forecastAdi.forecast(
      BuildConfig.DARKSKY_KEY,
      userLocation.latitude,
      userLocation.longitude,
      "us",
      arrayListOf("minutely", "hourly", "alerts", "flags").toString())
      .enqueue(object : Callback<DarkskyModel.Darksky> {
        override fun onResponse(call: Call<DarkskyModel.Darksky>, response: Response<DarkskyModel.Darksky>) {
          Timber.v("API Url call ${call.request().url()}")
          if (!response.isSuccessful || response.body() == null) {
            Timber.e("Error trying to fetch the user's location forecast.")
            // TODO display error message to the user
            return
          }
          // Pass information to the view
          darkSkyApiResponseLiveData.value = response.body()
        }

        override fun onFailure(call: Call<DarkskyModel.Darksky>, throwable: Throwable) {
          Timber.e(throwable, "Error trying to fetch the user's location forecast.")
        }
      })
  }

  //
  // Users last location
  //

  fun getUsersCurrentLocation() {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
    if (checkUserLocationPermission()) {
      fusedLocationClient.lastLocation.addOnSuccessListener(this).addOnFailureListener(this)
    }
  }

  override fun onSuccess(location: Location?) {
    if (location == null) {
      Timber.v("User location's null, falling back to default location.")
      locationNameLiveData.value = DEFAULT_LOCATION_NAME
    } else {
      Timber.v("User location: $location")
      userLocation = location
      getLocationName()
    }
    fetchForecastAtUserLocation()
  }

  override fun onFailure(exception: Exception) {
    Timber.e(exception, "Error trying to get last user location")
  }

  private fun checkUserLocationPermission(): Boolean {
    return if (ContextCompat.checkSelfPermission(appContext,
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      true
    } else {
      requestLocationPermissionLiveData.value = true
      false
    }
  }

  //
  // Reverse geocode
  //

  private fun getLocationName() {
    MapboxGeocoding.builder()
      .accessToken(BuildConfig.MAPBOX_ACCESS_TOKEN)
      .query(Point.fromLngLat(userLocation.longitude, userLocation.latitude))
      .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
      .build().enqueueCall(this)
  }

  override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
    if (response.isSuccessful && response.body()!!.features().isNotEmpty()) {
      Timber.v("Successfully reverse geocoded location")
      Timber.v("Location name: ${response.body()?.features()!![0].text()}")
      locationNameLiveData.value = response.body()?.features()!![0].text()
    }
  }

  override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
    Timber.e(throwable, "Error trying to reverse geocode location")
  }
}