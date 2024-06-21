package com.vimal.quiz.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.vimal.quiz.databinding.ActivitySplashBinding
import com.vimal.quiz.utils.Constant.SPLASH_TIME_OUT


class ActivitySplash : AppCompatActivity() {

    private var animation: TranslateAnimation? = null
    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())


        startAnimationsTitleTv()
        startAnimationsSubTitleTv()
        Handler(Looper.getMainLooper()).postDelayed({
            startAnimation()
        }, 1000.toLong())

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }


    private fun startAnimation() {
        binding.loading.visibility = View.VISIBLE
        binding.loadingProgress.visibility = View.VISIBLE
        animation = TranslateAnimation(0f, 1000f, 0f, 0f)
        animation?.duration = 500
        animation?.repeatCount = Animation.INFINITE
        binding.loading.startAnimation(animation)
    }

    private fun startAnimationsTitleTv() {
        animation = TranslateAnimation(-800f, 0f, 0f, 0f)
        animation?.duration = 350
        binding.titleTv.startAnimation(animation)
    }


    private fun startAnimationsSubTitleTv() {
        animation = TranslateAnimation(-10000f, 0f, 0f, 0f)
        animation?.duration = 500
        binding.subTitleTv.startAnimation(animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        animation?.cancel()
        animation = null
    }


}