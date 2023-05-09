package com.example.raul_lino_d.ui.map

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.raul_lino_d.MainActivity
import com.example.raul_lino_d.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var imageIV: ImageView // declare ImageView variable
    private lateinit var titleText: TextView // declare TextView variable
    private lateinit var desc: TextView // declare TextView variable
    private lateinit var parent: MainActivity



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {









        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        imageIV = view.findViewById(R.id.imageView) // find ImageView by ID from the inflated view
        titleText = view.findViewById(R.id.text_history_title) // find TextView by ID from the inflated view
        desc= view.findViewById(R.id.text_dashboard4)

        parent = (activity as MainActivity).getMain()


        val myButton = view.findViewById<ImageButton>(R.id.my_button)
        myButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        //get cache id of building
        val value = arguments!!.getInt("id")
        val folderNumber = "folder_${value}"
        val imgNumber = "img_${value}"
        val imgFilePath = "images/${folderNumber}/${imgNumber}0.jpg"
        val imgInputStream = requireContext().assets.open(imgFilePath)


        var titulo : String = value?.let { parent.buscarDados("titulo" , it) } as String
        var descricao : String = value?.let { parent.buscarDados("info" , it) } as String


        titleText.text = titulo
        desc.text = descricao


        val imgBitmap = BitmapFactory.decodeStream(imgInputStream)

        // on below line we are setting bitmap to our image view.
        imageIV.setImageBitmap(imgBitmap)
        return view
    }
    fun myButtonClick(view: View) {
        // Handle button click here
        // For example, to go back to the map fragment:
        findNavController().popBackStack()
    }


}