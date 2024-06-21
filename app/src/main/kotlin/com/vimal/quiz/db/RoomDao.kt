package com.vimal.quiz.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vimal.quiz.model.ModelQuizScore

@Dao
interface RoomDao {

    @Query("SELECT * FROM table_score ORDER BY id DESC LIMIT 30")
    fun getAllScore(): LiveData<List<ModelQuizScore>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertScore(modelList: ModelQuizScore)

    @Query("SELECT EXISTS (SELECT 1 FROM table_score WHERE id = :id)")
    fun isScore(id: Int): Boolean

    @Query("DELETE FROM table_score")
    fun deleteAllScore()

}