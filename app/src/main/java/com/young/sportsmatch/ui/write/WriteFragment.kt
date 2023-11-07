package com.young.sportsmatch.ui.write

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.young.sportsmatch.R
import com.young.sportsmatch.databinding.FragmentWriteBinding
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class WriteFragment : Fragment(), MapView.POIItemEventListener {

    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WriteViewModel by viewModels()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        mapView = MapView(requireActivity())
        binding.rlWriteLocationImg.addView(mapView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation(true)
        mapView.setPOIItemEventListener(this)
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

    private var isMapViewInitialized = false

    private fun searchMap() {
        binding.ivWriteLocation.setOnClickListener {
            val searchText = binding.etWriteLocation.text.toString()
            viewModel.getMap(searchText)
            viewModel.searchMap.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    if (!isMapViewInitialized) {
                        isMapViewInitialized = true
                    }
                    mapView.removeAllPOIItems()
                    for (place in response.documents) {
                        val marker = MapPOIItem()
                        marker.itemName = place.place_name
                        marker.tag = 0
                        marker.mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        marker.markerType = MapPOIItem.MarkerType.BluePin
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

                        mapView.addPOIItem(marker)

                        Log.d("map2", "$place")
                    }
                }
            }
        }
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?
    ) {
        val builder = AlertDialog.Builder(context)
        val itemList = arrayOf("확인", "취소")
        builder.setTitle("${poiItem?.itemName}")
        builder.setItems(itemList) { dialog, which ->
            when(which) {
                0 -> {
                    binding.etWriteLocation.text = Editable.Factory.getInstance().newEditable(poiItem?.itemName)
                }
                1 -> dialog.dismiss()   // 대화상자 닫기
            }
        }
        builder.show()
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }
}