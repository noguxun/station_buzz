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
import android.location.Location
import android.os.*
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.*
import android.media.RingtoneManager
import android.util.Log
import android.support.v4.content.LocalBroadcastManager
import android.os.CountDownTimer






class LocService : Service() {

    private val magicChannalId = 0x12345
    private val myBinder = MyLocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var isLocRequestOn = false
    var isLocTrackingOn = false

    var destinationLatitude = 35.681391
    var destinationLongitude = 139.766103

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentDistance = 0.0f

    val tag = "LocService"

    private lateinit var timer: CountDownTimer

    override fun onBind(intent: Intent): IBinder {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        return myBinder
    }

    inner class MyLocalBinder : Binder() {
        fun getService(): LocService {
            return this@LocService
        }
    }

    private fun onTimerFinished() {
        Log.i(tag, "onTimerFinished")
        makeLocationRequest()
    }

    private fun startTimer(seconds: Long){
        timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                // there is nothing to do
            }
        }.start()

        Log.i(tag, "timerStarted")
    }

    private fun onMyLocationUpdated(locationResult: LocationResult?){
        Log.i(tag, "onMyLocationUpdated")

        if (locationResult == null || locationResult.locations.size == 0) {
            Log.i(tag, "no result")
            return
        }

        val location = locationResult.locations[0]
        var dis = FloatArray(1)

        currentLatitude = location.latitude
        currentLongitude = location.longitude

        Location.distanceBetween(currentLatitude,currentLongitude, destinationLatitude, destinationLongitude, dis)
        currentDistance = dis[0]

        println("My Location ${location.longitude} ${location.latitude}, dis ${currentDistance}")
        sendLocationMessage()

        vibrate()
        // ring()

        cancelLocationRequest()
        startTimer(5L)
    }

    fun startLocTracking() {
        if (isLocTrackingOn) {
            println("nothing to do, tracking already on")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startNotificationAbove26()
        }
        else {
            startNotificationBelow26()
        }

        isLocTrackingOn = true

        makeLocationRequest()
    }

    fun stopLocTracking() {
        if (isLocTrackingOn == false) {
            println("nothing to do, tracking already off")
        }
        stopForeground(true)
        isLocTrackingOn = false
    }


    @SuppressLint("MissingPermission")
    private fun makeLocationRequest() {
        Log.i(tag,"make Location Request")
        if (isLocRequestOn == true) {
            println("Service already started, nothing to do")
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) = onMyLocationUpdated(locationResult)
        }

        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)



        isLocRequestOn = true

    }

    @SuppressLint("MissingPermission")
    private fun cancelLocationRequest() {
        fusedLocationClient.removeLocationUpdates(locationCallback)


        isLocRequestOn = false
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun startNotificationAbove26() {
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

    private fun startNotificationBelow26() {
        val notification = NotificationCompat.Builder(this).apply {
            setContentTitle("Train Buzz")
            setContentText("Train Buzz Notification")
            setSmallIcon(R.mipmap.ic_launcher)
        }.build()

        startForeground(magicChannalId, notification)
    }

    fun vibrate() {
        val v: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            println("vibrate new")
        } else {
            //deprecated in API 26
            v.vibrate(500)
            println("vibrate old ${v.hasVibrator()}")
        }
    }

    fun ring() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendLocationMessage() {
        val intent = Intent("BuzzStationLocationUpdate")

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
