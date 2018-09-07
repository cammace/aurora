package com.cammace.aurora

import android.app.Application
import timber.log.Timber.DebugTree
import timber.log.Timber

class AuroraApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    // Initialize Timber if in debug mode
    if (BuildConfig.DEBUG) {
      Timber.plant(DebugTree())
    }
  }
}