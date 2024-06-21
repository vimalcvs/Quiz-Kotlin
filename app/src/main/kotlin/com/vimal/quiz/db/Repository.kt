package com.vimal.quiz.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.vimal.quiz.model.ModelQuizScore
import com.vimal.quiz.utils.Utils

class Repository(context: Context) {

    private val roomDao: RoomDao = RoomDB.getDatabase(context).getRoomDao()
    private val scoreList: LiveData<List<ModelQuizScore>> = roomDao.getAllScore()

    fun allScore(): LiveData<List<ModelQuizScore>> {
        return scoreList
    }

    fun insertScore(model: ModelQuizScore) {
        try {
            object : Thread(Runnable { roomDao.insertScore(model) }) {
            }.start()
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }


    fun deleteAllScore() {
        try {
            object : Thread(Runnable { roomDao.deleteAllScore() }) {
            }.start()
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }


    companion object {
        private var repository: Repository? = null
        fun getInstance(context: Context): Repository? {
            if (repository == null) {
                repository = Repository(context)
            }
            return repository
        }
    }
}