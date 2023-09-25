package com.icm2330.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.icm2330.location.databinding.ActivityLocationSingleUseBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.logging.Logger


class LocationSingleUseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationSingleUseBinding

    companion object {
        // Setup del logger para esta clase
        private val TAG = LocationSingleUseActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    // Constantes de permisos
    private val LOCATION_PERMISSION_ID = 103
    var locationPerm = Manifest.permission.ACCESS_FINE_LOCATION

    // Variables de localización
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    // Variables de UI
    var latitud: TextView? = null
    var longitud: TextView? = null
    var elevacion: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSingleUseBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        latitud = binding.latitudeLabel
        longitud = binding.longitudeLabel
        elevacion = binding.elevationLabel

        // Inicializar el FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission(
            this,
            locationPerm,
            "Permiso para utilizar la localización",
            LOCATION_PERMISSION_ID
        )
        initView()
    }

    private fun requestPermission(
        context: Activity,
        permission: String,
        justification: String,
        id: Int
    ) {
        // Verificar si no hay permisos
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show()
            }
            // Request the permission
            ActivityCompat.requestPermissions(context, arrayOf(permission), id)
        }
    }

    private fun initView() {
        if (ContextCompat.checkSelfPermission(
                this,
                locationPerm
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.warning("Failed to getting the location permission :(")
        } else {
            logger.info("Success getting the location permission :)")
            mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                logger.info("onSuccessLocation")
                if (location != null) {
                    logger.info("Longitud: ${location.longitude}")
                    logger.info("Latitud: ${location.latitude}")
                    logger.info("Elevación:  ${location.altitude}")
                    latitud!!.text = String.format("%s", location.latitude)
                    longitud!!.text = String.format("%s", location.longitude)
                    elevacion!!.text = String.format("%s", location.altitude)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_ID) {
            initView()
        }
    }

    fun refresh(view: View?) {
        initView()
    }

}