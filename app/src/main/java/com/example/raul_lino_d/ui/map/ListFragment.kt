package com.example.raul_lino_d.ui.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.raul_lino_d.MainActivity
import com.example.raul_lino_d.R
import org.json.JSONArray
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class ListFragment : Fragment() {
    private lateinit var parent: MainActivity
    private lateinit var textView: TextView
    private lateinit var list: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent = activity as MainActivity
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        textView = rootView.findViewById(R.id.text_dashboard4)
        // Obter o valor do argumento "itinerario" passado para o fragmento
        val value = arguments?.getInt("itinerario")
        Log.e("ListFragment", "Valor recebido: $value")
        list = value?.let { getLista(it) }.toString()  // Obter a lista com base no valor do itinerário
        // Configurar o botão
        val myButton: ImageButton = rootView.findViewById(R.id.my_button)
        myButton.setOnClickListener {
            findNavController().navigateUp() // Navegar para trás, fechar o fragmento atual
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Definir o texto da TextView com a lista
        val novoTexto = list
        textView.text = novoTexto
    }

    /**
     *Função que obtém a lista histórica com base no valor do itinerário e no nome do campo.
     */
    private fun listaHistIt(it: Int, fieldName: String): String {
        // Obter os dados com base no nome do campo (fieldName) e no valor do itinerário (it)
        val dados: JSONArray = parent.buscarDados(fieldName, it + 21) as JSONArray
        val resultado = StringBuilder()
        // Construir o resultado da lista histórica
        for (j in 0 until dados.length()) {
            val id = dados.get(j)
            val loc: String = parent.buscarDados("localizacao", id as Int) as String
            val id1: String = parent.buscarDados("ano", id) as String
            resultado.append("Ano: $id1, -  $loc\n\n")
        }
        return resultado.toString()
    }

    /***
     * Função que obtém a lista com base no valor do itinerário.
     */
    private fun getLista(value: Int): String {
        // Determinar o nome do campo com base no valor do itinerário
        val fieldName = if (value == 1) "ListaIdsIt1" else "ListaIdsIt2"
        return listaHistIt(value, fieldName) // Obter a listaHistIt com base no campo e no valor do itinerário

    }

    fun myButtonClick(view: View) {
        findNavController().popBackStack()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

