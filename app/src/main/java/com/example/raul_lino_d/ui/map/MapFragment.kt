package com.example.raul_lino_d.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

class MapFragment : Fragment(), LocationListener {

    private lateinit var parent: MainActivity
    private var _binding: FragmentMapBinding? = null
    private lateinit var map: MapView
    private lateinit var locationManager: LocationManager
    lateinit var userMarker: Marker
    lateinit var point: GeoPoint

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        ViewModelProvider(this).get(MapViewModel::class.java)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        parent = activity as MainActivity
        locationManager = parent.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                parent,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5f, this)
        Configuration.getInstance().userAgentValue = parent.packageName
        map = root.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.zoomTo(17.0)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true) // para poder fazer zoom com os dedos
        val compassOverlay = CompassOverlay(parent, map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)
        userMarker = Marker(map)
        userMarker.icon = resources.getDrawable(R.drawable.ponto_preto)
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(userMarker)
        for (i in 1 until 18) {
            val dados: JSONArray = parent.buscarDados("coordenadas", i) as JSONArray
            point = GeoPoint(dados.get(0) as Double, dados.get(1) as Double)
            val startMarker = Marker(map)
            var texto : CharSequence = parent.buscarDados("titulo" , i) as CharSequence
            println(texto)
            startMarker.position = point
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            var markerWindow:MarkerWindow =  MarkerWindow(map, parent)
            markerWindow.setText(texto.toString())
            startMarker.infoWindow = markerWindow
            startMarker.infoWindow
            val bitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.localizao_verde)
          val dr: Drawable = BitmapDrawable(
            resources,
            bitmap?.let {
                Bitmap.createScaledBitmap(
                     it,
                     (48.0f * resources.displayMetrics.density).toInt(),
                     (48.0f * resources.displayMetrics.density).toInt(),
                     true
                    )
                }
            )
            startMarker.icon = dr
            map.overlays.add(startMarker)
            map.invalidate()
            //clicar no pino
            startMarker.setOnMarkerClickListener { marker, mapView ->
                // Create a new instance of your fragment
                val fragment = HistoryFragment()
                val args = Bundle()
                args.putInt("id", i)
                fragment.arguments = args
                //para nao dar erro ao clicar nos botões
                val transaction =
                    parentFragmentManager.beginTransaction() // começa uma transação do FragmentManager
                transaction.replace(
                    R.id.nav_host_fragment_activity_main,
                    fragment
                ) // substitui o fragment atual pelo novo fragment
                transaction.setReorderingAllowed(true) // permite que o back stack seja restaurado como uma operação atômica (é necessário para usar addToBackStack)
                transaction.addToBackStack(null) // adiciona a transação ao back stack, com um nome nulo
                transaction.commit() // confirma a transação
                // Return true to indicate that the click event has been handled
                true
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            map.controller.setCenter(point)
        }, 1000)// espera 1 Segundo para centrar o mapa

        return root
    }

    class MarkerWindow: InfoWindow {
        private lateinit var parent: MainActivity
        constructor(mapView: MapView, parent: MainActivity):super(R.layout.info_window, mapView) {
            this.parent = parent
        }
        override fun onOpen(item: Any?) {
            closeAllInfoWindowsOn(mapView)
            val texto = mView.findViewById<TextView>(R.id.TextoPin)

            texto.setOnClickListener {
                close()
            }
        }
        override fun onClose() {
// para usar caso seja necessário
        }

        fun setText(txt:String) {
            var txtView = mView.findViewById<TextView>(R.id.TextoPin)
            txtView.setText(txt)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLocationChanged(location: Location) {
        val point = GeoPoint(location.latitude, location.longitude)
        userMarker.position = point
        map.overlays.add(userMarker)
        map.controller.setCenter(point)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(parent, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(parent, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}