package com.vimal.quiz.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_score")
class ModelQuizScore(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: String,
    var score: Int,
    var question: Int,
    var category: String
) : Serializable
