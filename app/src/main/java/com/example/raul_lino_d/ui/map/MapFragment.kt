package com.example.raul_lino_d.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.raul_lino_d.MainActivity
import com.example.raul_lino_d.R
import com.example.raul_lino_d.databinding.FragmentMapBinding
import org.json.JSONArray
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.Polyline
import kotlin.math.log

class MapFragment : Fragment(), LocationListener {

    private lateinit var parent: MainActivity
    private var _binding: FragmentMapBinding? = null
    private lateinit var map: MapView
    private lateinit var locationManager: LocationManager
    lateinit var userMarker: Marker
    lateinit var point: GeoPoint
    lateinit var pointold: GeoPoint
    private val markersToPaint = mutableListOf<Marker>()
    private var showing1 : Boolean = false
    private var showing2 : Boolean = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Get the NavController
        navController = NavHostFragment.findNavController(this)
       // ViewModelProvider(this).get(MapViewModel::class.java)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        parent = activity as MainActivity

        //definir as coordenadas de cada itinerario
        val geoPoints1 = getGeoPoints(20).toMutableList() // Assign the returned list to geoPoints1
        val geoPoints2 = getGeoPoints(21).toMutableList() // Assign the returned list to geoPoints2

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


        binding.button.setOnClickListener {

            itinerario1()
            //pinta os makers que são comuns ao itenerario 2
            paintMarkers(markersToPaint, geoPoints1 as ArrayList<GeoPoint>, showing1)
            //permite voltar a pintar os makers de verde
            showing1 = !showing1
        }

        binding.button2.setOnClickListener {

            itinerario2()
            //pinta os makers que são comuns ao itenerario 2
            paintMarkers(markersToPaint, geoPoints2 as ArrayList<GeoPoint>, showing2)
            //permite voltar a pintar os makers de verde
            showing2 = !showing2
        }

        for (i in 1 until 18) {
            val dados: JSONArray = parent.buscarDados("coordenadas", i) as JSONArray
            point = GeoPoint(dados.get(0) as Double, dados.get(1) as Double)
            val startMarker = Marker(map)
            var texto : CharSequence = parent.buscarDados("titulo" , i) as CharSequence
            startMarker.position = point
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            var markerWindow:MarkerWindow =  MarkerWindow(map, parent,i,navController)
            markerWindow.setText(texto.toString())
            startMarker.infoWindow = markerWindow
            startMarker.infoWindow
            markersToPaint.add(startMarker)
            val bitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.localizao_verde)
          val dr: Drawable = BitmapDrawable(
            resources,
            bitmap?.let {
                Bitmap.createScaledBitmap(
                     it,
                     (60.0f * resources.displayMetrics.density).toInt(),
                     (60.0f * resources.displayMetrics.density).toInt(),
                     true
                    )
                }
            )
            startMarker.icon = dr
            map.overlays.add(startMarker)
            map.invalidate()
            //clicar no pino
        }

        Handler(Looper.getMainLooper()).postDelayed({
            //map.controller.setCenter(point)
        }, 1000)// espera 1 Segundo para centrar o mapa
        return root
    }

    class MarkerWindow(
        mapView: MapView,
        private val parent: MainActivity,
        private val fragmentId: Int,
        private val navController: NavController

    ) : InfoWindow(R.layout.info_window, mapView) {

        override fun onOpen(item: Any?) {
            closeAllInfoWindowsOn(mapView)
            val texto = mView.findViewById<TextView>(R.id.TextoPin)


            texto.setOnClickListener {
                close()
                val fragment = HistoryFragment()
                val args = Bundle()
                args.putInt("id", fragmentId)
                fragment.arguments = args
                navController.navigate(R.id.navigation_history, args)
            }
        }

        fun setText(txt: String) {
            val txtView = mView.findViewById<TextView>(R.id.TextoPin)
            txtView.text = txt
        }

        override fun onClose() {
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
        Handler(Looper.getMainLooper()).postDelayed({
        map.controller.setCenter(point)
        }, 1000)
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



    //permite alterar a cor dos marcadores de cada edíficio com base se este se econtra no itinerário escolhido ou não
    fun paintMarkers(markers: MutableList<Marker>, geoPoints: ArrayList<GeoPoint>, showing: Boolean) {
        //iterar todos os marcadores e verificar se estes se encontram na lista de marcadores do itinerário passado por parametro
        for (marker in markers) {
            val markerGeoPoint = marker.position
            if (geoPoints.contains(markerGeoPoint)) {
                val bitmap: Bitmap? = if (showing) {
                    BitmapFactory.decodeResource(resources, R.drawable.localizao_verde)
                } else {
                    BitmapFactory.decodeResource(resources, R.drawable.localizao_amarela)
                }

                val drawable = BitmapDrawable(
                    resources,
                    bitmap?.let {
                        Bitmap.createScaledBitmap(
                            it,
                            (60.0f * resources.displayMetrics.density).toInt(),
                            (60.0f * resources.displayMetrics.density).toInt(),
                            true
                        )
                    }
                )

                marker.icon = drawable
            }
        }
        map.invalidate()
    }

    //vai buscar as coordenadas de cate itenarario com base no index
    private fun getGeoPoints(index: Int): List<GeoPoint> {
        val targetList = mutableListOf<GeoPoint>()
        val dados: JSONArray = parent.buscarDados("coordenadas", index) as JSONArray
        for (j in 0 until dados.length()) {
            val c: JSONArray = dados.get(j) as JSONArray
            val latitude = c.get(0) as Double
            val longitude = c.get(1) as Double
            val point = GeoPoint(latitude, longitude)
            targetList.add(point)
        }
        return targetList
    }



    fun itinerario1(){
        val dados: JSONArray = parent.buscarDados("coordenadas", 18) as JSONArray
        for (j in 0 until dados.length()) {
            val coor:JSONArray = dados.get(j) as JSONArray
            val geoPoints = ArrayList<GeoPoint>();
            if (j!=0) {
                pointold = point
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
                geoPoints.add(pointold)
                geoPoints.add(point)
                val line = Polyline()
                line.setPoints(geoPoints);
                map.overlays.add(line);
                map.invalidate()
            } else {
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            //map.controller.setCenter(point)
        }, 1000)// espera 1 Segundo para centrar o mapa
    }

    fun itinerario2(){
        val dados: JSONArray = parent.buscarDados("coordenadas", 19) as JSONArray
        for (j in 0 until dados.length()) {
            val coor:JSONArray = dados.get(j) as JSONArray
            val geoPoints = ArrayList<GeoPoint>();
            if (j!=0) {
                pointold = point
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
                geoPoints.add(pointold)
                geoPoints.add(point)
                val line = Polyline()
                line.setPoints(geoPoints)
                map.overlays.add(line)
                map.invalidate()
            } else {
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
            }
        }


    }

}