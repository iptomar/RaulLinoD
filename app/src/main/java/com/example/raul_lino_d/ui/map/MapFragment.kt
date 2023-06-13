package com.example.raul_lino_d.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MapFragment : Fragment(), LocationListener {
    private lateinit var parent: MainActivity
    private var _binding: FragmentMapBinding? = null
    private lateinit var map: MapView
    private lateinit var locationManager: LocationManager
    private lateinit var userMarker: Marker
    private lateinit var point: GeoPoint
    private lateinit var pointold: GeoPoint
    private val markersToPaint = mutableListOf<Marker>()
    private var showing1: Boolean = false
    private var showing2: Boolean = false
    private var itinerarioSelecionado: Int = 0 // Valor inicial para nenhum itinerário selecionado
    private var itinerario1Visible = false
    private var itinerario2Visible = false
    private val itinerario1Lines = ArrayList<Polyline>()
    private val itinerario2Lines = ArrayList<Polyline>()
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Get the NavController
        navController = NavHostFragment.findNavController(this)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        parent = activity as MainActivity
        // Definir as coordenadas de cada itinerario
        val geoPoints1 = getGeoPoints(20).toMutableList() // Assign the returned list to geoPoints1
        val geoPoints2 = getGeoPoints(21).toMutableList() // Assign the returned list to geoPoints2
        locationManager = parent.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                parent,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            @Suppress("DEPRECATION")
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
        @Suppress("DEPRECATION")
        userMarker.icon = resources.getDrawable(R.drawable.ponto_preto)
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(userMarker)
        binding.button.setOnClickListener {
            itinerario1()
            //pinta os makers que são comuns ao itenerario 2
            paintMarkers(markersToPaint, geoPoints1 as ArrayList<GeoPoint>, showing1)
            //permite voltar a pintar os makers de verde
            map.overlays.add(userMarker)
            map.invalidate()
            //volta a pintar o marcador do utilizador
            showing1 = !showing1
        }
        binding.button2.setOnClickListener {
            itinerario2()
            //pinta os makers que são comuns ao itenerario 2
            paintMarkers(markersToPaint, geoPoints2 as ArrayList<GeoPoint>, showing2)
            //permite voltar a pintar os makers de verde
            map.overlays.add(userMarker)
            map.invalidate()
            //volta a pintar o marcador do utilizador
            showing2 = !showing2
        }
        binding.button3.setOnClickListener {
            enviarCacheItinerarioSelecionado()
        }
        for (i in 1 until 18) {
            val dados: JSONArray = parent.buscarDados("coordenadas", i) as JSONArray
            point = GeoPoint(dados.get(0) as Double, dados.get(1) as Double)
            val startMarker = Marker(map)
            val texto: CharSequence = parent.buscarDados("titulo", i) as CharSequence
            startMarker.position = point
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            val markerWindow = MarkerWindow(map, i, navController)
            markerWindow.setText(texto.toString())
            startMarker.infoWindow = markerWindow
            startMarker.infoWindow
            markersToPaint.add(startMarker)
            val bitmap: Bitmap? =
                BitmapFactory.decodeResource(resources, R.drawable.localizao_verde)
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
        }
        repintar()
        paintMarkers(markersToPaint, geoPoints1 as ArrayList<GeoPoint>, !showing1)
        paintMarkers(markersToPaint, geoPoints2 as ArrayList<GeoPoint>, !showing2)
        return root
    }

    class MarkerWindow(
        mapView: MapView,
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

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
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
    private fun paintMarkers(
        markers: MutableList<Marker>,
        geoPoints: ArrayList<GeoPoint>,
        showing: Boolean
    ) {
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
                //remover e voltar a adicionar o marker para aparecer por cima da rota
                marker.remove(map)
                marker.icon = drawable
                map.overlays.add(marker)
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

    /**
     * funções itinerario1() e itinerario2()
     * estas funcões são responsaveis por fazer a dinamização das cores das linhas dos itinerários
     * elas verificam se as linhas estão visiveis:
     * se sim remove as linhas
     * se não pinta as linhas
     */

    private fun itinerario1() {
        if (itinerario1Visible) {
            // Remover as linhas do itinerário 1
            for (line in itinerario1Lines) {
                map.overlays.remove(line)
            }
            map.invalidate()
            itinerario1Visible = false
            // Atualizar o valor do itinerário selecionado
            itinerarioSelecionado = if (itinerario2Visible) 3 else 0
        } else {
            pintarit1()
        }
    }

    private fun itinerario2() {
        if (itinerario2Visible) {
            // Remover as linhas do itinerário 2
            for (line in itinerario2Lines) {
                map.overlays.remove(line)
            }
            map.invalidate()
            itinerario2Visible = false
            itinerarioSelecionado =
                if (itinerario1Visible) 3 else 0 // Atualizar o valor do itinerário selecionado
        } else {
            pintarit2()
        }
    }

    /**
     * Enviar os dados de cache do itinerário selecionado.
     *
     * Verifica se o itinerário selecionado é o 1 ou o 2 e, em caso afirmativo, redireciona
     * para o ecra de destino, enviado os dados de cache correspondentes.
     */
    private fun enviarCacheItinerarioSelecionado() {
        // Verificar se o itinerário selecionado é o 1 ou o 2
        // Criar um Bundle para armazenar os dados a serem enviados
        val bundle = Bundle()
        // Adicionar o valor do itinerário selecionado ao Bundle
        bundle.putInt("itinerario", itinerarioSelecionado)
        // Redireciona para o ecra de destino (R.id.navigation_list) passando o Bundle como parâmetro
        navController.navigate(R.id.navigation_list, bundle)
    }


    /**
     * Funções pintarti1() e pintarit2()
     * nestas funções vamos pintar linhas entre vários pontos já pré definidos no JSON.file
     * estes pontos foram esolhidos especificamente de forma a poder criar um itinerario pelas ruas da cidade
     */
    private fun pintarit1() {
        // Adicionar as linhas do itinerário 1
        val dados: JSONArray = parent.buscarDados("coordenadas", 18) as JSONArray
        for (j in 0 until dados.length()) {
            val coor: JSONArray = dados.get(j) as JSONArray
            val geoPoints = ArrayList<GeoPoint>()
            if (j != 0) {
                pointold = point
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
                geoPoints.add(pointold)
                geoPoints.add(point)
                val line = Polyline()
                line.setPoints(geoPoints)
                @Suppress("DEPRECATION")
                line.color = Color.rgb(250, 171, 30) // Set the color directly on the Polyline object
                itinerario1Lines.add(line) // Add the line to the itinerario1Lines list
                map.overlays.add(line)
                map.invalidate()
            } else {
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
            }
        }
        itinerario1Visible = true
        itinerarioSelecionado = if (itinerario2Visible) 3 else 1 // Atualizar o valor do itinerário selecionado
    }

    private fun pintarit2() {
        // Adicionar as linhas do itinerário 2
        val dados: JSONArray = parent.buscarDados("coordenadas", 19) as JSONArray
        for (j in 0 until dados.length()) {
            val coor: JSONArray = dados.get(j) as JSONArray
            val geoPoints = ArrayList<GeoPoint>()
            if (j != 0) {
                pointold = point
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
                geoPoints.add(pointold)
                geoPoints.add(point)
                val line = Polyline()
                line.setPoints(geoPoints)
                @Suppress("DEPRECATION")
                line.color =
                    Color.rgb(250, 171, 30) // Set the color directly on the Polyline object
                itinerario2Lines.add(line) // Add the line to the itinerario2Lines list
                map.overlays.add(line)
                map.invalidate()
            } else {
                point = GeoPoint(coor.get(0) as Double, coor.get(1) as Double)
            }
        }
        itinerario2Visible = true
        itinerarioSelecionado =
            if (itinerario1Visible) 3 else 2 // Atualizar o valor do itinerário selecionado
    }

    //repinta os itinerarios
    private fun repintar() {
        if (itinerario1Visible) {
            pintarit1()
        }
        if (itinerario2Visible) {
            pintarit2()
        }
    }
}