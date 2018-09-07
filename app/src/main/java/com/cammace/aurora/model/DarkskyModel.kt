package com.cammace.aurora.model

object DarkskyModel {

  data class Darksky(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currently: Currently)

  data class Currently(
    val time: Long
  )
}