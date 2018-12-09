package com.xgu.trainbuz

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.android.gms.location.*

class LocService : Service() {

    private val myBinder = MyLocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent): IBinder {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        return myBinder
    }

    inner class MyLocalBinder : Binder() {
        fun getService(): LocService {
            return this@LocService
        }
    }

    @SuppressLint("MissingPermission")
    fun makeLocationRequest() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    println("My Location ${location.longitude} ${location.latitude}")
                }
            }
        }

        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }
}
