package com.xgu.trainbuz

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_buzz_start.*
import android.support.design.widget.Snackbar





// https://github.com/googlesamples/android-play-location/blob/master/LocationUpdatesForegroundService/app/src/main/java/com/google/android/gms/location/sample/locationupdatesforegroundservice/MainActivity.java
class BuzzStartActivity : AppCompatActivity() {

    private val tag = this::class.java.simpleName
    private var locService: LocService? = null

    private val locServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            val binder = service as LocService.MyLocalBinder
            locService = binder.getService()

            println("LocService connected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locService = null
            println("LocService disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buzz_start)

        buttonStartBuzz.setOnClickListener {
            Log.d(tag,"start buzz clicked")

            if (requestLocationPermission() == true) {
                locService?.makeLocationRequest()
            }
        }


        val intent = Intent(this, LocService::class.java)
        bindService(intent, locServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun requestLocationPermission(): Boolean{
        val locPermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (locPermission == PackageManager.PERMISSION_GRANTED) {
            Log.i(tag, "Location permission granted already.")
            return true
        }

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        val showPermission = shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (showPermission) {
            Log.i(tag, "Displaying permission rationale to provide additional context.")
            Snackbar.make(root_layout, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE).apply {
                setAction(R.string.requestion_permission) {
                    ActivityCompat.requestPermissions(this@BuzzStartActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
                }

                show()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            println("granted")
            locService?.makeLocationRequest()
        } else {
            println("Please allow the permission")
        }
    }
}
