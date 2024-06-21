package com.vimal.quiz.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.vimal.quiz.utils.Constant.NIGHT_MODE
import com.vimal.quiz.utils.Constant.NIGHT_PREFERENCE

class SharedPrefer(private val context: Context) {

    fun saveNightMode(nightMode: Int) {
        val editor = context.getSharedPreferences(NIGHT_PREFERENCE, Context.MODE_PRIVATE).edit()
        editor.putInt(NIGHT_MODE, nightMode)
        editor.apply()
    }

    fun loadNightMode(): Int {
        return context.getSharedPreferences(NIGHT_PREFERENCE, Context.MODE_PRIVATE)
            .getInt(NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}