package com.uyoung.sportsmatch.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.uyoung.sportsmatch.R
import com.uyoung.sportsmatch.data.model.Constants
import com.uyoung.sportsmatch.databinding.ActivitySettingBinding
import com.uyoung.sportsmatch.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
        showLoadingState()
        submit()

        binding.ivBack.setOnClickListener {
            moveToHome()
        }
    }

    private fun submit() {
        binding.ivAdd.setOnClickListener {
            getContent.launch("image/*")
        }
        binding.btnAddUser.setOnClickListener {
            val nickname = binding.etNickName.text.toString()
            val regex = Regex("^[a-zA-Z0-9]{5,}+$|^[가-힣]{2,6}$")
            if (nickname.isNotEmpty()) {
                if (nickname.matches(regex)) {
                    viewModel.addUser(nickname)
                } else {
                    showToast(getString(R.string.setting_failed))
                }
            } else {
                showToast(getString(R.string.write_failed))
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
            viewModel.userInfo.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { user ->
                    if (user != null && !userLoaded) {
                        userLoaded = true
                        val nickName = user.nickName
                        val imageUrl = user.imageUrl
                        val storageReference: StorageReference =
                            FirebaseStorage.getInstance().reference
                        val imageRef = storageReference.child(imageUrl)
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            viewModel.updateSelectedImage(uri)
                            selectedImage(uri)
                        }.addOnFailureListener { e ->

                        }
                        binding.etNickName.setText(nickName)
                    } else {
                        // 사용자 정보를 가져오지 못한 경우에 대한 처리
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showLoadingState() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { state ->
                binding.btnAddUser.isEnabled = !state

                if (state) {
                    binding.workingProgressIndicator.visibility = View.VISIBLE
                    delay(Constants.DELAY_DURATION)
                    showToast(getString(R.string.profile_change_succeed))
                    moveToHome()
                } else {
                    binding.workingProgressIndicator.visibility = View.GONE
                    binding.btnAddUser.isEnabled = true
                }
            }
        }
    }
}