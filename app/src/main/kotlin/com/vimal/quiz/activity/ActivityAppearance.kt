package com.vimal.quiz.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.vimal.quiz.R
import com.vimal.quiz.databinding.ActivityAppearanceBinding
import com.vimal.quiz.utils.SharedPrefer

class ActivityAppearance : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAppearanceBinding
    private val sharedPrefer: SharedPrefer by lazy { SharedPrefer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppearanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        loadNightMode()
    }

    private fun loadNightMode() {
        val nightMode = sharedPrefer.loadNightMode()
        AppCompatDelegate.setDefaultNightMode(nightMode)
        setSelected(nightMode)
    }


    private fun setupToolbar() {
        binding.tvToolbar.text = getString(R.string.appearance)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupClickListeners() {
        binding.llDarkMode.setOnClickListener(this)
        binding.llLightMode.setOnClickListener(this)
        binding.llSystemMode.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val selectedMode = when (view) {
            binding.llDarkMode -> AppCompatDelegate.MODE_NIGHT_YES
            binding.llLightMode -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        sharedPrefer.saveNightMode(selectedMode)
        AppCompatDelegate.setDefaultNightMode(selectedMode)
        setSelected(selectedMode)
    }

    private fun setSelected(nightMode: Int) {
        binding.ivDarkMode.setImageResource(R.drawable.ic_selected_no_state)
        binding.ivLightMode.setImageResource(R.drawable.ic_selected_no_state)
        binding.ivSystemMode.setImageResource(R.drawable.ic_selected_no_state)
        when (nightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> binding.ivDarkMode.setImageResource(R.drawable.ic_selected_white_blue_bg)
            AppCompatDelegate.MODE_NIGHT_NO -> binding.ivLightMode.setImageResource(R.drawable.ic_selected_white_blue_bg)
            else -> binding.ivSystemMode.setImageResource(R.drawable.ic_selected_white_blue_bg)
        }
    }
}