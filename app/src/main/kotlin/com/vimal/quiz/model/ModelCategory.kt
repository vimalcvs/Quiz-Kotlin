package com.vimal.quiz.model

import java.io.Serializable

data class ModelCategory(
    val id: Int,
    val name: String,
    val categoryImage: Int,
    val category: String,
    val totalQuestions: String,
    val colorPrimary: String,
    val colorDark: String,
    val colorLight: String
) : Serializable