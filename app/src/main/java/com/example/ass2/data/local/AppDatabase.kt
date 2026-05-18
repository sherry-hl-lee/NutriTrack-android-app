package com.example.ass2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(
    entities = [Meal::class, User::class], // 👈 加 User
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun userDao(): UserDao // 👈 必须加

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}