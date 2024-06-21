package com.vimal.quiz.utils

import com.vimal.quiz.BuildConfig

object Config {

    const val API_KEY = "bb6hfqQ90x2aUEtLZSlN3Vz4gdoOWFCAvbKj7DmGRp1c8us"
    private val ADMIN_PANEL_URL: String = BuildConfig.ADMIN_URL

    @JvmStatic
    fun getAdminUrl(): String {
        return ADMIN_PANEL_URL
    }
}
