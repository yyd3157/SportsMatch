package com.young.sportsmatch.ui.write

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.young.sportsmatch.R
import com.young.sportsmatch.data.model.MarkerPlace
import com.young.sportsmatch.databinding.FragmentWriteBinding
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.Calendar

@AndroidEntryPoint
class WriteFragment : Fragment(), MapView.POIItemEventListener {

    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WriteViewModel by viewModels()
    private lateinit var mapView: MapView
    private var selectedMarker: MapPOIItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        mapView = MapView(requireActivity())
        binding.rlWriteLocationImg.addView(mapView)
        binding.ivWriteDate.setOnClickListener { onDataPicker() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActivityMenu(true)
        mapView.setPOIItemEventListener(this)
        searchMap()
        submit()
        setUpSportsTypeSpinner()
        setUpTypeSpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideActivityMenu(false)
    }

    private fun hideActivityMenu(boolean: Boolean) {
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val writeButton = activity?.findViewById<ExtendedFloatingActionButton>(R.id.write_button)
        if (boolean){
            bottomNavigation?.visibility = View.GONE
            writeButton?.hide()
        } else {
            bottomNavigation?.visibility = View.VISIBLE
            writeButton?.show()
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
                    if (response.documents.isNotEmpty()) {
                        val firstPlace = response.documents[0]
                        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(firstPlace.y.toDouble(), firstPlace.x.toDouble()), 7, true)
                    }
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
                    Log.d("potItem","$poiItem")
                    selectedMarker = poiItem
                }
                1 -> dialog.dismiss()   // 대화상자 닫기
            }
        }
        builder.show()
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    private fun submit() {
        binding.btnAddPost.setOnClickListener {
            val title = binding.etWriteTitle.text.toString()
            val category = binding.spinnerWriteCategory.selectedItem.toString()
            val type = binding.spinnerWriteType.selectedItem.toString()
            val date = binding.etWriteDate.text.toString()
            val placeName : String? = selectedMarker?.itemName
            val x: String = selectedMarker?.mapPoint?.mapPointGeoCoord?.longitude.toString()
            val y: String = selectedMarker?.mapPoint?.mapPointGeoCoord?.latitude.toString()
            val content = binding.etWriteContent.text.toString()

            if (title.isNotEmpty()&&category.isNotEmpty()&&date.isNotEmpty()&&placeName != null&&x.isNotEmpty()&&y.isNotEmpty()) {
                viewModel.addPost(title, category, type, date, MarkerPlace(placeName, x, y), content)
            } else {
                // 빈칸 조건에 따른 처리 예정
            }
        }
    }

    private fun onDataPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        context?.let { it1 ->
            DatePickerDialog(it1, { _, year, month, day ->
                run {
                    binding.etWriteDate.setText(year.toString() + "." + (month + 1).toString() + "." + day.toString() + " / ")
                }
            }, year, month, day)
        }?.show()
    }

    private fun setUpSportsTypeSpinner() {
        val spinner = binding.spinnerWriteCategory
        val category = resources.getStringArray(R.array.category_array)
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, category)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }

    private fun setUpTypeSpinner() {
        val spinner = binding.spinnerWriteType
        val category = resources.getStringArray(R.array.type_array)
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, category)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }
}