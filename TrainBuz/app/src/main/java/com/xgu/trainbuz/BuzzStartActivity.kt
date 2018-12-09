package com.xgu.trainbuz

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_buzz_start.*
import android.support.design.widget.Snackbar





// https://github.com/googlesamples/android-play-location/blob/master/LocationUpdatesForegroundService/app/src/main/java/com/google/android/gms/location/sample/locationupdatesforegroundservice/MainActivity.java
class BuzzStartActivity : AppCompatActivity() {

    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buzz_start)

        buttonStartBuzz.setOnClickListener {
            Log.d(tag,"start buzz clicked")

            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        val locPermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (locPermission == PackageManager.PERMISSION_GRANTED) {
            Log.i(tag, "Location permission granted already.")
            return
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            println("granted")

        } else {
            println("Please allow the permission")
        }
    }
}
