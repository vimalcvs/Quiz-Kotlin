package com.vimal.quiz.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vimal.quiz.model.ModelQuizScore

@Database(
    entities = [ModelQuizScore::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDB : RoomDatabase() {

    abstract fun getRoomDao(): RoomDao

    companion object {
        @Volatile
        private var instance: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "database_v1.db"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}