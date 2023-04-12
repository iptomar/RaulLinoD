package com.example.raul_lino_d



import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.raul_lino_d.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import com.example.raul_lino_d.ui.map.MapFragment
import java.io.InputStream


class MainActivity : AppCompatActivity() {



    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionsIfNecessary(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)



        val jsonData=applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "dados",
                "raw" ,applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val outputJsonString=JSONObject(jsonData)


        val dados = outputJsonString.getJSONArray("dados") as JSONArray
        for (i in 0 until dados.length()){
            //val id = posts.getJSONObject(i).get("id")
            val nome = dados.getJSONObject(i).get("localizacao")

        //código para colocar as variaveis no sitio
        // binding.teste.text = "$nome"

        }



    }

    private fun requestPermissionsIfNecessary(permissions:Array<out String>) {
        val permissionsToRequest = ArrayList<String>();
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
// Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(arrayOf<String>()),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public fun getMain(): MainActivity {
        return this
    }
}