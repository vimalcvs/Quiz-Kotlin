package com.vimal.quiz.model

import java.io.Serializable

data class ModelColor(
    val id: Int,
    val colorPrimary: String,
    val colorDark: String,
    val colorLight: String
) : Serializable