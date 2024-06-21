package com.vimal.quiz

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.vimal.quiz.utils.SharedPrefer
import com.vimal.quiz.utils.Utils

class MyApplication : Application() {

    private val sharedPrefer: SharedPrefer by lazy { SharedPrefer(this) }

    override fun onCreate() {
        super.onCreate()
        try {
            AppCompatDelegate.setDefaultNightMode(sharedPrefer.loadNightMode())
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }

}