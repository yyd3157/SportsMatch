package com.young.sportsmatch.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.young.sportsmatch.R
import com.young.sportsmatch.data.model.Post
import com.young.sportsmatch.databinding.FragmentDetailBinding
import com.young.sportsmatch.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActivityMenu(true)
        mapView = MapView(requireActivity())
        binding.rlDetailLocationImg.addView(mapView)
        setLayout()
        viewModel.loadBookmarkState()
        lifecycleScope.launch {
            Log.d("DetailFragment", "Start collecting bookmarkStatus")
            viewModel.bookmarkStatus.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect { posts ->
                Log.d("DetailFragment", "Bookmark status changed: $posts")
                val isBookmark = posts.get(args.post.hashCode().toString()) ?: false
                Log.d("DetailFragment1", "isBookmark: $isBookmark")
                binding.ivDetailFavorites.isSelected = isBookmark
            }
            Log.d("DetailFragment", "Stop collecting bookmarkStatus")
        }
    }


    private fun setLayout() {
        val post = args.post
        binding.viewModel = viewModel
        binding.post = post
        val imageUrl = post.user.imageUrl
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child(imageUrl)
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            binding.ivProfileImage.load(uri) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_default_picture)
                error(R.drawable.ic_default_picture)
            }
        }.addOnFailureListener { }
        getMap()
    }

    private fun getMap() {
        val marker = MapPOIItem()
        val place = args.post.markerPlace
        marker.itemName = place.place_name
        marker.tag = 0
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble()), 4, true
        )
    }

    private fun hideActivityMenu(boolean: Boolean) {
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val writeButton = activity?.findViewById<ExtendedFloatingActionButton>(R.id.write_button)
        val backButton = activity?.findViewById<ImageView>(R.id.iv_back)
        if (boolean){
            bottomNavigation?.visibility = View.GONE
            backButton?.visibility = View.VISIBLE
            writeButton?.hide()
        } else {
            bottomNavigation?.visibility = View.VISIBLE
            backButton?.visibility = View.GONE
            writeButton?.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideActivityMenu(false)
    }
}