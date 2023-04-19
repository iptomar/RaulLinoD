package com.example.raul_lino_d.ui.map

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.raul_lino_d.R
import java.io.File

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val imgInputStream = requireContext().assets.open("images/folder_1/img_11.jpg")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        imageIV = view.findViewById(R.id.imageView) // find ImageView by ID from the inflated view
        // on below line we are checking if the image file exist or not.

        // on below line we are creating an image bitmap variable
        // and adding a bitmap to it from image file.
        val imgBitmap = BitmapFactory.decodeStream(imgInputStream)

        // on below line we are setting bitmap to our image view.
        imageIV.setImageBitmap(imgBitmap)
        return view
    }
}