package com.example.raul_lino_d.ui.map

import ViewPageAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.raul_lino_d.MainActivity
import com.example.raul_lino_d.R
import java.io.IOException
import java.io.InputStream

class HistoryFragment : Fragment() {
    private lateinit var titleText: TextView
    private lateinit var desc: TextView
    private lateinit var parent: MainActivity
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = NavHostFragment.findNavController(this)
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        titleText = view.findViewById(R.id.text_history_title)
        desc = view.findViewById(R.id.text_dashboard4)
        parent = (activity as MainActivity).getMain()

        val myButton = view.findViewById<ImageButton>(R.id.my_button)
        myButton.setOnClickListener {
            navController.navigate(R.id.navigation_map)
        }

        //referenciar o viewpager para mais tarde levar o adapter
        val viewPager = view.findViewById<ViewPager>(R.id.ViewPager)
        //referenciar a cache
        val value = arguments?.getInt("id") ?: 0
        //número da pasta do respetivo edificio
        val folderNumber = "folder_$value"

        val imgNumbers = mutableListOf<String>()
        var i = 0
        var imgFilePath: String
        var imgInputStream: InputStream?

        //loop para cada pasta de imagens
        do {
            //nome da imagem
            val imgNumber = "img_${value}${i}"
            //nome da pasta
            imgFilePath = "images/$folderNumber/$imgNumber.jpg"
            imgInputStream = try {
                requireContext().assets.open(imgFilePath)
            } catch (e: IOException) {
                null
            }

            if (imgInputStream != null) {
                imgNumbers.add(imgNumber)
            }

            i++
        } while (imgInputStream != null)

        val imageList = mutableListOf<Bitmap>()
        imgNumbers.forEach { imgNumber ->
            imgFilePath = "images/$folderNumber/$imgNumber.jpg"
            imgInputStream = requireContext().assets.open(imgFilePath)

            val imgBitmap = BitmapFactory.decodeStream(imgInputStream)
            //array de imgBitmap
            imageList.add(imgBitmap)
        }

        //enviar o array para o adapter
        val sliderAdapter = ViewPageAdapter(imageList)
        //atribuir o adapter ao viewpagar
        viewPager.adapter = sliderAdapter

        val titulo: String = value.let { parent.buscarDados("titulo", it) } as String
        val descricao: String = value.let { parent.buscarDados("info", it) } as String

        titleText.text = titulo
        desc.text = descricao

        return view
    }

    fun myButtonClick(view: View) {
        findNavController().popBackStack()
    }
}
