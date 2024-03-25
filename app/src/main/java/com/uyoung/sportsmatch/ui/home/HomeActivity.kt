package com.uyoung.sportsmatch.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.uyoung.sportsmatch.R
import com.uyoung.sportsmatch.databinding.ActivityHomeBinding
import com.uyoung.sportsmatch.ui.login.LoginActivity
import com.uyoung.sportsmatch.ui.setting.SettingActivity
import com.uyoung.sportsmatch.ui.setting.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: SettingViewModel by viewModels()
    private var userLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivSetting.setOnClickListener {
            binding.dlHome.openDrawer(GravityCompat.END)
        }

        binding.writeButton.setOnClickListener {
            observeUserInfo()
        }

        binding.ivBack.setOnClickListener {
            val navController = supportFragmentManager.findFragmentById(R.id.home_container)?.findNavController()
            navController?.popBackStack()
        }

        val navController = supportFragmentManager.findFragmentById(R.id.home_container)?.findNavController()
        navController?.let {
            binding.bottomNavigationView.setupWithNavController(it)
        }

        binding.nvSetting.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.setting_profile -> {
                    moveToSetting()
                }

                R.id.setting_logout -> {
                    viewModel.logout()
                    lifecycleScope.launch {
                        viewModel.logout.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { isSuccess ->
                            if (isSuccess) {
                                showToast(getString(R.string.logout_succeed))
                                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                                finish()
                            } else {
                                // 실패 시 처리
                            }
                        }
                    }
                }
            }
            true
        }
        val navControllers = supportFragmentManager.findFragmentById(R.id.home_container)?.findNavController()
        navControllers?.addOnDestinationChangedListener { _, destination, _ ->
            // 현재 destination이 HOME인지 확인하여 글쓰기 버튼 가시성을 조절합니다.
            setWriteButtonVisibility(destination.id)
        }
    }

    override fun onBackPressed() {
        if (binding.dlHome.isDrawerOpen(GravityCompat.END)) {
            binding.dlHome.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    private fun moveToSetting() {
        startActivity(Intent(this, SettingActivity::class.java))
    }

    private fun navigateToWrite() {
        val navController = findNavController(R.id.home_container)
        navController.navigate(R.id.navigaion_write)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun observeUserInfo() {
        lifecycleScope.launch {
            viewModel.getUser()
            viewModel.userInfo.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { user ->
                    if (user != null && !userLoaded) {
                        userLoaded = true
                        navigateToWrite()
                    } else {
                        showToast(getString(R.string.write_failed_profile))
                    }
                }
        }
    }

    private fun setWriteButtonVisibility(destinationId: Int) {
        val writeButton = findViewById<ExtendedFloatingActionButton>(R.id.write_button)
        if (destinationId == R.id.navigation_home) {
            writeButton.show()
        } else {
            writeButton.hide()
        }
    }
}