package com.example.raul_lino_d.ui.map


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.raul_lino_d.MainActivity
import com.example.raul_lino_d.R
import com.example.raul_lino_d.databinding.FragmentMapBinding
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.infowindow.InfoWindow.closeAllInfoWindowsOn


class MapFragment : Fragment() {

    private lateinit var parent: MainActivity
    private var _binding: FragmentMapBinding? = null




    private lateinit var map : MapView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
                ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        parent = (activity as MainActivity).getMain()

        Configuration.getInstance().userAgentValue = parent.getPackageName()
        map = root.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.zoomTo(17.0)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true) // para poder fazer zoom com os dedos
        var compassOverlay = CompassOverlay(parent, map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)
        for (i in 1 until 18){
        var dados : JSONArray = parent.buscarDados("coordenadas" , i) as JSONArray
        var point = GeoPoint(dados.get(0) as Double, dados.get(1)as Double)
        var startMarker = Marker(map)
        startMarker.position = point
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            startMarker.infoWindow = MarkerWindow(map, parent)
        map.overlays.add(startMarker)
        Handler(Looper.getMainLooper()).postDelayed({
            map.controller.setCenter(point)
        }, 1000) }// espera 1 Segundo para centrar o mapa


        return root
    }

    class MarkerWindow: InfoWindow {
        private lateinit var parent: MainActivity
        constructor(mapView: MapView, parent: MainActivity):super(R.layout.info_window, mapView) {
            this.parent = parent
        }
        override fun onOpen(item: Any?) {
            closeAllInfoWindowsOn(mapView)
            val olaButton = mView.findViewById<Button>(R.id.ola_button)
            olaButton.setOnClickListener {
                Toast.makeText(parent,"Ola IPT", Toast.LENGTH_LONG).show()
            }
            mView.setOnClickListener {
                close()
            }
        }
        override fun onClose() {
// para usar caso seja necessário
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}