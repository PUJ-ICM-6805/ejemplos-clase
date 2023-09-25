package com.icm2330.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.icm2330.location.databinding.ActivityLocationAwareBinding
import com.icm2330.location.model.CustomLocation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import org.json.JSONArray
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.Date
import java.util.logging.Logger


class LocationAwareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationAwareBinding

    companion object {
        // Setup del logger para esta clase
        private val TAG = LocationSingleUseActivity::class.java.name

        // Constantes
        const val RADIUS_OF_EARTH_KM = 6371.0
        const val DORADO_LAT = 4.598102
        const val DORADO_LON = -74.076099
        const val REQUEST_CHECK_SETTINGS = 201
    }

    private val logger = Logger.getLogger(TAG)

    // Variables de perrmisos
    private val LOCATION_PERMISSION_ID = 103
    var locationPerm = Manifest.permission.ACCESS_FINE_LOCATION

    // Variables de localización
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    var mCurrentLocation: Location? = null
    private val mStoredLocations = JSONArray()

    // Views
    private var latitud: TextView? = null
    private var longitud: TextView? = null
    private var elevacion: TextView? = null
    private var distancia: TextView? = null
    private var save: Button? = null
    var listaUbicaciones: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationAwareBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        // Inflates
        latitud = binding.latitudeValue
        longitud = binding.longitudeValue
        elevacion = binding.elevationValue
        distancia = binding.distanceValue
        save = binding.saveLocation
        listaUbicaciones = binding.listaUbicaciones
        save!!.setOnClickListener { view ->
            writeJSONObject()
            if (mCurrentLocation != null) {
                val tv = TextView(view.context)
                val cadena =
                    "Lat: " + mCurrentLocation!!.latitude.toString() + " - Long: " + mCurrentLocation!!.longitude.toString() + " on " + Date(
                        System.currentTimeMillis()
                    ).toString()
                tv.text = cadena
                listaUbicaciones!!.addView(tv)
            }
        }
        // onCreate
        mLocationRequest = createLocationRequest()
        // Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            "El permiso es necesario para acceder a la localización",
            LOCATION_PERMISSION_ID
        )
        mLocationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                logger.info("Location update in the callback: $location")
                if (location != null) {
                    mCurrentLocation = location
                    logger.info(location.latitude.toString())
                    logger.info(location.longitude.toString())
                    logger.info(location.altitude.toString())
                    logger.info(
                        distance(
                            location.latitude,
                            location.longitude,
                            DORADO_LAT,
                            DORADO_LON
                        ).toString() + " km"
                    )
                    latitud!!.text = location.latitude.toString()
                    longitud!!.text = location.longitude.toString()
                    elevacion!!.text = location.altitude.toString()
                    distancia!!.text = distance(
                        location.latitude,
                        location.longitude,
                        DORADO_LAT,
                        DORADO_LON
                    ).toString() + " km"
                }
            }
        }
        // Pasar a onResume!!
        turnOnLocationAndStartUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest!!, mLocationCallback!!, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setMinUpdateDistanceMeters(5F)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }

    private fun requestPermission(
        context: Activity,
        permiso: String,
        justificacion: String,
        idCode: Int
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                permiso
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, justificacion, Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_ID -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        "Ya hay permiso para acceder a la localización",
                        Toast.LENGTH_LONG
                    ).show()
                    turnOnLocationAndStartUpdates()
                } else {
                    Toast.makeText(this, "No hay permiso :(", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun turnOnLocationAndStartUpdates() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            mLocationRequest!!
        )
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            this
        ) { locationSettingsResponse: LocationSettingsResponse? ->
            startLocationUpdates() // Todas las condiciones para recibiir localizaciones
        }
        task.addOnFailureListener(this) { e ->
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->                         // Location setttings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult()
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@LocationAwareActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: SendIntentException) {
                        // Ignore the error
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates()
                } else {
                    Toast.makeText(
                        this,
                        "Sin acceso a localización. Hardware deshabilitado",
                        Toast.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = Math.sin(latDistance / 2) *
                Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val result = RADIUS_OF_EARTH_KM * c
        return Math.round(result * 100.0) / 100.0
    }

    private fun writeJSONObject() {
        val myLocation = CustomLocation()
        myLocation.fecha = Date(System.currentTimeMillis())
        myLocation.latitud = mCurrentLocation!!.latitude
        myLocation.longitud = mCurrentLocation!!.latitude
        mStoredLocations.put(myLocation.toJSON())
        var output: Writer? = null
        val filename = "locations.json"
        try {
            val file = File(baseContext.getExternalFilesDir(null), filename)
            logger.info("Ubicación de archivo: $file")
            output = BufferedWriter(FileWriter(file))
            output.write(mStoredLocations.toString())
            output.close()
            Toast.makeText(applicationContext, "Location saved", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

}