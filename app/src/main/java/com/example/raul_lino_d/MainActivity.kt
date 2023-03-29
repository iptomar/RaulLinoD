package com.example.raul_lino_d

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.raul_lino_d.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        val jsonData=applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "Dados",
                "raw" ,applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val outputJsonString=JSONObject(jsonData)


        val dados = outputJsonString.getJSONArray("dados") as JSONArray
        for (i in 0 until dados.length()){
            //val id = posts.getJSONObject(i).get("id")
            val nome = dados.getJSONObject(i).get("localizacao")

            binding.teste.text = "$nome"

        }



    }
}