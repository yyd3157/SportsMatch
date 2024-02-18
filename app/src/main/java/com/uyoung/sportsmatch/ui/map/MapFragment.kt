package com.uyoung.sportsmatch.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.uyoung.sportsmatch.R
import com.uyoung.sportsmatch.databinding.FragmentMapBinding
import com.uyoung.sportsmatch.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private val mapViewModel: MapViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    val REQUEST_LOCATION_PERMISSION = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActivityMenu(true)
        setLayout()
        binding.rvMap.bringToFront()
        binding.ivCurrentLocation.bringToFront()
        mapView = MapView(requireActivity())
        binding.rlMapLocationImg.addView(mapView)
        getPostMap()
        binding.ivCurrentLocation.setOnClickListener {
            setCurrentLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideActivityMenu(false)
        _binding = null
    }

    private fun getPostMap() {
        lifecycleScope.launch {
            mapViewModel.getPostList()
            mapViewModel.items.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { response ->
                    if (response != null) {
                        for ((key, post) in response) {
                            val postPlace = listOf(post.markerPlace)
                            for (place in postPlace) {
                                val marker = MapPOIItem()
                                marker.itemName = place.place_name
                                marker.tag = 0
                                marker.mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                                marker.markerType = MapPOIItem.MarkerType.BluePin
                                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

                                mapView.addPOIItem(marker)
                            }

                        }
                    }
                }
        }
    }

    private fun setLayout() {
        homeViewModel.loadBookmarkState()
        val adapter = MapListAdapter ({ post ->
            val selectPlace = post.markerPlace
            mapView.setMapCenterPointAndZoomLevel(
                MapPoint.mapPointWithGeoCoord(selectPlace.y.toDouble(), selectPlace.x.toDouble()), 4, true
            )
        }, homeViewModel)
        binding.rvMap.adapter = adapter
        mapViewModel.getPostList()
        lifecycleScope.launch {
            mapViewModel.items.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { post ->
                    val allPostList = post?.values?.toList()
                    adapter.submitList(allPostList)
                }
        }
    }

    private fun hideActivityMenu(boolean: Boolean) {
        val writeButton = activity?.findViewById<ExtendedFloatingActionButton>(R.id.write_button)
        val backButton = activity?.findViewById<ImageView>(R.id.iv_back)
        if (boolean){
            writeButton?.hide()
            backButton?.visibility = View.GONE
        } else {
            writeButton?.show()
        }
    }

    private fun setCurrentLocation() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val userCurLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (userCurLocation != null) {
                val uLatitude = userCurLocation.latitude
                val uLongitude = userCurLocation.longitude
                val uCurPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)

                Log.d("location", "$uCurPosition")
                mapView.setMapCenterPointAndZoomLevel(
                    uCurPosition, 4, true
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
}