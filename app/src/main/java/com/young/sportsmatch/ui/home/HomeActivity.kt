package com.young.sportsmatch.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.young.sportsmatch.R
import com.young.sportsmatch.databinding.ActivityHomeBinding
import com.young.sportsmatch.ui.setting.SettingActivity
import com.young.sportsmatch.ui.write.WriteFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivSetting.setOnClickListener {
            moveToSetting()
        }

        binding.writeButton.setOnClickListener {
            navigateToWrite()
        }

        binding.ivBack.setOnClickListener {
            val navController = supportFragmentManager.findFragmentById(R.id.home_container)?.findNavController()
            navController?.popBackStack()
        }

        val navController =
            supportFragmentManager.findFragmentById(R.id.home_container)?.findNavController()
        navController?.let {
            binding.bottomNavigationView.setupWithNavController(it)
        }
    }

    private fun moveToSetting() {
        startActivity(Intent(this, SettingActivity::class.java))
    }

    private fun navigateToWrite() {
        val navController = findNavController(R.id.home_container)
        navController.navigate(R.id.navigaion_write)
    }
}