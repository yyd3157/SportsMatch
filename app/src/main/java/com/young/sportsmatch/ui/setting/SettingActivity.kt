package com.young.sportsmatch.ui.setting

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.young.sportsmatch.R
import com.young.sportsmatch.SportsMatchApplication
import com.young.sportsmatch.data.source.SettingRepository
import com.young.sportsmatch.data.source.remote.UserRemoteDataSource
import com.young.sportsmatch.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingViewModel by viewModels {
        SettingViewModel.provideFactory(
            SettingRepository(
                UserRemoteDataSource(
                    SportsMatchApplication.appContainer.provideApiClient()
                )
            )
        )
    }
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImage(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeUserInfo()
        submit()
    }

    private fun submit() {
        binding.ivAdd.setOnClickListener {
            getContent.launch("image/*")
        }
        binding.btnAddUser.setOnClickListener {
            val nickname = binding.etNickName.text.toString()
            if (nickname.isNotEmpty()) {
                viewModel.addUser(nickname)
            } else {
                // 닉네임이 비어있을 때 처리
            }
        }
    }

    private fun selectedImage(uri: Uri) {
        viewModel.updateSelectedImage(uri)
        binding.ivProfileImage.load(uri) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_default_picture)
            error(R.drawable.ic_default_picture)
        }
    }

    private fun observeUserInfo() {
        viewModel.getUser()
        viewModel.userInfo.observe(this) { user ->
            if (user != null) {
                val nickName = user.nickName
                val imageUrl = user.imageUrl
                val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageReference.child(imageUrl)
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("ImageLoad", "Image URL: $uri")
                    binding.ivProfileImage.load(uri) {
                        transformations(CircleCropTransformation())
                        placeholder(R.drawable.ic_default_picture)
                        error(R.drawable.ic_default_picture)
                    }
                }.addOnFailureListener { e ->
                    Log.e("FirebaseStorage", "Error getting download URL: $e")
                }
                binding.etNickName.setText(nickName)
            } else {
                // 사용자 정보를 가져오지 못한 경우에 대한 처리
            }
        }
    }
}