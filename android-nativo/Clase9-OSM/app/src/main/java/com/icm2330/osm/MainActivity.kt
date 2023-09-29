package com.icm2330.osm

import androidx.appcompat.app.AppCompatActivity

import android.app.UiModeManager
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.util.Log

import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MyActivity"
    }

    lateinit var map: MapView

    //starting point for the map
    var latitude = 4.6269938175930525
    var longitude = -74.0638974995316
    var startPoint = GeoPoint(latitude, longitude)
    lateinit var longPressedMarker: Marker
    lateinit var roadManager: RoadManager

    // Attribute
    lateinit var roadOverlay: Polyline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main)
        map = findViewById(R.id.osmMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        //Add Marker
        val markerPoint = GeoPoint(latitude, longitude)
        val marker = Marker(map)
        marker.title = "Bogotá"
        marker.subDescription = "Marcador en Bogotá"
        val myIcon = resources.getDrawable(R.drawable.location_red, this.theme)
        marker.icon = myIcon
        marker.position = markerPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.getOverlays().add(marker)
        map.getOverlays().add(createOverlayEvents())
        roadManager = OSRMRoadManager(this, "ANDROID")
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        val mapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(startPoint)
        val uiManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        }
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun createMarker(p: GeoPoint, title: String?, desc: String?, iconID: Int): Marker {
        lateinit var marker: Marker
        if (map != null) {
            marker = Marker(map)
            if (title != null) {
                marker.title = title
            }
            if (desc != null) {
                marker.subDescription = desc
            }
            if (iconID != 0) {
                val myIcon = resources.getDrawable(iconID, this.theme)
                marker.icon = myIcon
            }
            marker.position = p
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        return marker
    }

    private fun createOverlayEvents(): MapEventsOverlay {
        return MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                longPressOnMap(p)
                return true
            }
        })
    }

    private fun longPressOnMap(p: GeoPoint) {
        if (longPressedMarker != null) {
            map.overlays.remove(longPressedMarker)
        }
        longPressedMarker = createMarker(p, "location", null, R.drawable.location_blue)
        map.overlays.add(longPressedMarker)
    }

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        val road: Road = roadManager.getRoad(routePoints)
        Log.i(TAG, "Route length: " + road.mLength + " klm")
        Log.i(TAG, "Duration: " + road.mDuration / 60 + " min")
        if (map != null) {
            if (roadOverlay != null) {
                map!!.overlays.remove(roadOverlay)
            }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay.outlinePaint.color = Color.RED
            roadOverlay.outlinePaint.strokeWidth = 10f
            map.overlays.add(roadOverlay)
        }
    }
}