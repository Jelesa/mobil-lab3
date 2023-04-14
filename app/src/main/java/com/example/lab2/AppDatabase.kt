package com.example.lab2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Categories::class, RecipesCategories::class, Recipe::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getDao(): Dao

    /*companion object{
        fun getDatabase(context: Context): AppDatabase{
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "recipeDatabase"
            ).build()
        }
    }*/
}