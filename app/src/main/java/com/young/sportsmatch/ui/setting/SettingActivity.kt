package com.young.sportsmatch.ui.setting

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import coil.load
import coil.transform.CircleCropTransformation
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
}