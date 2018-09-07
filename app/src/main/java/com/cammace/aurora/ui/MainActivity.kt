package com.cammace.aurora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.cammace.aurora.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    // Setup toolbar and hide title
    setSupportActionBar(main_toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)

    getUsersCurrentLocation()
  }

  private fun getUsersCurrentLocation() {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    fusedLocationClient.lastLocation.
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
}
