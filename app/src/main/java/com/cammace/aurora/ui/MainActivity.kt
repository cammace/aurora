package com.cammace.aurora.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cammace.aurora.R
import com.cammace.aurora.databinding.ActivityMainBinding
import com.cammace.aurora.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

const val REQUEST_COARSE_LOCATION = 5678

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var viewModel: MainActivityViewModel

  // Weather icon map
  private val weatherIconMap = HashMap<String, Drawable>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    addObservers()

    // Setup toolbar and hide title
    setSupportActionBar(toolbar_mainActivity)
    supportActionBar?.setDisplayShowTitleEnabled(false)

    mapWeatherIcons()

    viewModel.getUsersCurrentLocation()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_toolbar, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == R.id.action_search) {
      Toast.makeText(this, "search clicked", Toast.LENGTH_LONG).show()
    }

    return super.onOptionsItemSelected(item)
  }

  private fun addObservers() {
    viewModel.requestLocationPermissionLiveData.observe(this, Observer { shouldRequestPermission ->
      if (shouldRequestPermission) {
        Timber.v("requesting permission")
        requestPermissions()
      }
    })

    viewModel.locationNameLiveData.observe(this, Observer { locationName ->
      binding.locationName = locationName
    })

    viewModel.darkSkyApiResponseLiveData.observe(this, Observer { darkSkyModel ->
      binding.currentCondition = darkSkyModel.currently

      // Bind the current weather icon
      if (darkSkyModel.currently.icon != null) {
        binding.currentConditionIcon = weatherIconMap[darkSkyModel.currently.icon]
      }
    })
  }

  private fun requestPermissions() {
    ActivityCompat.requestPermissions(this,
      arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE_LOCATION)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == REQUEST_COARSE_LOCATION && grantResults.isNotEmpty()
      && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Timber.v("User gave location permission, continue with getting user's last location.")
      viewModel.getUsersCurrentLocation()
    } else {
      Timber.v("User refused to give location permission. Continue using the default location.")
    }
  }

  /**
   * Since the Dark Sky API response icon doesn't directly map to our drawables, we have to do so manually.
   */
  private fun mapWeatherIcons() {
    weatherIconMap["clear-day"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_clear_day)!!
    weatherIconMap["clear-night"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_clear_night)!!
    weatherIconMap["rain"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_rain)!!
    weatherIconMap["snow"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_snow)!!
    weatherIconMap["sleet"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_sleet)!!
    weatherIconMap["wind"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_wind)!!
    weatherIconMap["fog"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_fog)!!
    weatherIconMap["cloudy"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_cloudy)!!
    weatherIconMap["partly-cloudy-day"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_partly_cloudy_day)!!
    weatherIconMap["partly-cloudy-night"] = ContextCompat.getDrawable(this, R.drawable.ic_weather_party_cloudy_night)!!
  }
}
