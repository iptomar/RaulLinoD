package com.example.raul_lino_d.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.raul_lino_d.MainActivity
import com.example.raul_lino_d.R
import com.example.raul_lino_d.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay

class MapFragment : Fragment() {
    private lateinit var parent: MainActivity
    private var _binding: FragmentMapBinding? = null
    private lateinit var map : MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        parent = (activity as MainActivity).getMain()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(parent)
        Configuration.getInstance().userAgentValue = parent.getPackageName()
        map = root.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.zoomTo(17.0)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true) // para poder fazer zoom com os dedos
        var compassOverlay = CompassOverlay(parent, map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)
        getLocation()
        return root
    }

    private fun addMarker(point: GeoPoint) {
        var startMarker = Marker(map)
        startMarker.position = point
        startMarker.icon = ContextCompat.getDrawable(parent, R.drawable.icon_ponto_preto)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(startMarker)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLocation() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {location: Location? ->
                var point = GeoPoint(location?.latitude!!, location?.longitude!!)
                Log.e("Latitude:", location?.latitude.toString())
                Log.e("Longitude:", location?.longitude.toString())
                addMarker(point)
                // espera 1 Segundo para centrar o mapa
                Handler(Looper.getMainLooper()).postDelayed({
                    map.controller.setCenter(point)
                }, 1000)
            }
    }
}