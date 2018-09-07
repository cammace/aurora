package com.cammace.aurora.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import timber.log.Timber
import java.lang.Exception

@JvmField val NEW_YORK_LOCATION = Location("default").apply {
  latitude = 40.6973
  longitude = -74.2195
}

class MainActivityViewModel(application: Application): AndroidViewModel(application),
  OnSuccessListener<Location>, OnFailureListener {

  // LiveData
  val requestLocationPermission = MutableLiveData<Boolean>()

  private var userLocation: Location = NEW_YORK_LOCATION
  private val appContext = getApplication() as Context

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
    } else {
      Timber.v("User location: $location")
      userLocation = location
      getLocationName()
    }
  }

  override fun onFailure(exception: Exception) {
    Timber.e(exception, "Error trying to get last user location")
  }

  private fun checkUserLocationPermission(): Boolean {
    return if (ContextCompat.checkSelfPermission(appContext,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      true
    } else {
      requestLocationPermission.value = true
      false
    }
  }

  /**
   * Reverse geocode to get the city name to display.
   */
  private fun getLocationName() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}