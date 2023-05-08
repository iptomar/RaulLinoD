package com.example.raul_lino_d.ui.map


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        savedInstanceState: Bundle?,
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
        map.overlays.add(startMarker)

            //clicar no pino
            startMarker.setOnMarkerClickListener { marker, mapView ->
                // Create a new instance of your fragment
                val fragment = HistoryFragment()

                val args = Bundle()
                args.putInt("id", 2)
                fragment.setArguments(args)

                //para nao dar erro ao clicar nos botões
                val transaction = parentFragmentManager.beginTransaction() // começa uma transação do FragmentManager
                transaction.replace(R.id.nav_host_fragment_activity_main, fragment) // substitui o fragment atual pelo novo fragment
                transaction.setReorderingAllowed(true) // permite que o back stack seja restaurado como uma operação atômica (é necessário para usar addToBackStack)
                transaction.addToBackStack(null) // adiciona a transação ao back stack, com um nome nulo
                transaction.commit() // confirma a transação



                // Return true to indicate that the click event has been handled
                true
            }


        Handler(Looper.getMainLooper()).postDelayed({
            map.controller.setCenter(point)
        }, 1000) }// espera 1 Segundo para centrar o mapa


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}