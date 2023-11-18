package com.young.sportsmatch.ui.setting

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.young.sportsmatch.R
import com.young.sportsmatch.databinding.ActivitySettingBinding
import com.young.sportsmatch.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImage(uri)
            }
        }
    private var userLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeUserInfo()
        submit()
        logout()
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
                // 닉네임 조건에 따른 처리 예정
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
        lifecycleScope.launch {
            viewModel.getUser()
            viewModel.userInfo.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { user ->
                if (user != null && !userLoaded) {
                    userLoaded = true
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
                    selectedImage(Uri.parse(imageUrl))
                } else {
                    // 사용자 정보를 가져오지 못한 경우에 대한 처리
                }
            }
        }

}

    private fun logout() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            lifecycleScope.launch {
                viewModel.logout.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { isSuccess ->
                    if (isSuccess) {
                        showToast(getString(R.string.logout_succeed))
                        startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                        finish()
                    } else {

                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}