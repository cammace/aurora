package com.cammace.aurora.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    addObservers()

    // Setup toolbar and hide title
    setSupportActionBar(main_toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)

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
    viewModel.requestLocationPermission.observe(this, Observer { shouldRequestPermission ->
      if (shouldRequestPermission) {
        requestPermissions()
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
}
