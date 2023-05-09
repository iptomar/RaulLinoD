package com.example.raul_lino_d.ui.map


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
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
        map.invalidate();
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