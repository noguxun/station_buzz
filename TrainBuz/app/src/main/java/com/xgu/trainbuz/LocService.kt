package com.xgu.trainbuz

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.*

class LocService : Service() {

    private val magicChannalId = 0x12345
    private val myBinder = MyLocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var isServiceStarted = false

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
        if (isServiceStarted == true) {
            println("Service already started, nothing to do")
            return
        }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startServiceAbove26()
        }
        else {
            startServiceBelow26()
        }

        isServiceStarted = true
    }

    @SuppressLint("MissingPermission")
    fun cancelLocationRequest() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)

        isServiceStarted = false
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun startServiceAbove26() {
        val channelId = "com.xgu.trainbuz"
        val channelName = "Train Buzz"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(magicChannalId, notification)
    }

    private fun startServiceBelow26() {
        val notification = NotificationCompat.Builder(this).apply {
            setContentTitle("Train Buzz")
            setContentText("Train Buzz Notification")
            setSmallIcon(R.mipmap.ic_launcher)
        }.build()


        startForeground(magicChannalId, notification)
    }
}
