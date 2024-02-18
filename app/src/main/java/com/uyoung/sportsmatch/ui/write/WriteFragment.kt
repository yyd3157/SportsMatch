package com.uyoung.sportsmatch.ui.write

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.uyoung.sportsmatch.R
import com.uyoung.sportsmatch.data.model.MarkerPlace
import com.uyoung.sportsmatch.databinding.FragmentWriteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        showLoadingState()
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
        val backButton = activity?.findViewById<ImageView>(R.id.iv_back)
        val settingButton = activity?.findViewById<ImageView>(R.id.iv_setting)
        val settingDrawer = activity?.findViewById<NavigationView>(R.id.nv_setting)
        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.dl_home)
        if (boolean){
            backButton?.visibility = View.VISIBLE
            settingButton?.visibility = View.GONE
            settingDrawer?.visibility = View.GONE
            bottomNavigation?.visibility = View.GONE
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            writeButton?.hide()
        } else {
            backButton?.visibility = View.GONE
            settingButton?.visibility = View.VISIBLE
            settingDrawer?.visibility = View.VISIBLE
            bottomNavigation?.visibility = View.VISIBLE
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            writeButton?.show()
        }
    }

    private var isMapViewInitialized = false

    private fun searchMap() {
        binding.ivWriteLocation.setOnClickListener {
            val searchText = binding.etWriteLocation.text.toString()

            lifecycleScope.launch {
                viewModel.getMap(searchText)
                    viewModel.searchMap.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collect { response ->
                        if (response != null) {
                            if (!isMapViewInitialized) {
                                isMapViewInitialized = true
                            }
                            mapView.removeAllPOIItems()
                            if (response.documents.isNotEmpty()) {
                                val firstPlace = response.documents[0]
                                mapView.setMapCenterPointAndZoomLevel(
                                    MapPoint.mapPointWithGeoCoord(firstPlace.y.toDouble(), firstPlace.x.toDouble()), 7, true
                                )
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

            if (title.isNotEmpty()&&date.isNotEmpty()&&placeName != null&&x.isNotEmpty()&&y.isNotEmpty()) {
                if (!viewModel.isLoading.value) {
                    viewModel.addPost(
                        title,
                        category,
                        type,
                        date,
                        MarkerPlace(placeName, x, y),
                        content
                    )
                    showToast(getString(R.string.write_succeed))
                }
            } else {
                showToast(getString(R.string.write_failed))
            }
        }
    }

    private fun onDataPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        context?.let {
            val datePickerDialog = DatePickerDialog(it, { _, year, month, day ->
                run {
                    val selectedDate = String.format("%d.%d.%d / ", year, month + 1, day)
                    showTimePicker(selectedDate) // 시간 선택 다이얼로그 호출
                }
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun showTimePicker(selectedDate: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        context?.let {
            val timePickerDialog = TimePickerDialog(it, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                binding.etWriteDate.setText("$selectedDate$selectedTime")
            }, hour, minute, true)
            timePickerDialog.show()
        }
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

    private fun navigationToHome() {
        val action = WriteFragmentDirections.actionGlobalHome()
        findNavController().navigate(action)
    }

    private fun showLoadingState() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { state ->
                if (state) {
                    binding.workingProgressIndicator.visibility = View.VISIBLE
                    navigationToHome()
                } else {
                    binding.workingProgressIndicator.visibility = View.GONE
                }
            }
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}