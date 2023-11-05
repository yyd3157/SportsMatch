package com.young.sportsmatch.ui.write

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.young.sportsmatch.R
import com.young.sportsmatch.databinding.FragmentWriteBinding
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class WriteFragment : Fragment() {

    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)

        val mapView = MapView(requireActivity())
        binding.rlWriteLocationImg.addView(mapView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation(true)
        searchMap()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(false)
    }

    private fun hideBottomNavigation(boolean: Boolean) {
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (boolean){
            bottomNavigation?.visibility = View.GONE
        } else {
            bottomNavigation?.visibility = View.VISIBLE
        }
    }

    private fun searchMap() {
        binding.ivWriteLocation.setOnClickListener {
            val searchText = binding.etWriteLocation.text.toString()
            viewModel.getMap(searchText)
            viewModel.searchMap.observe(viewLifecycleOwner){
                Log.d("map2", "$it")
            }
        }
    }
}