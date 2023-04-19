package com.example.raul_lino_d.ui.paginicial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.raul_lino_d.databinding.FragmentPaginicialBinding

class PagInicialFragment : Fragment() {

    private var _binding: FragmentPaginicialBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val pagInicialViewModel =
                ViewModelProvider(this).get(PagInicialViewModel::class.java)

        _binding = FragmentPaginicialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pagInicialViewModel.text.observe(viewLifecycleOwner) {
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}