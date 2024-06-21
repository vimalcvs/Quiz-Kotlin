package com.vimal.quiz.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ModelQuizLive(application: Application) : AndroidViewModel(application) {
    var score = 0
    var currentQueIndex = -1
    var questions: ArrayList<ModelQuizQuestion>? = null


    fun incrementScore() {
        score += 1
    }

    fun nextQuestion(): ModelQuizQuestion {
        currentQueIndex++
        return questions!![currentQueIndex]
    }

    fun totalQuestions(): Int {
        return questions!!.size
    }
}
