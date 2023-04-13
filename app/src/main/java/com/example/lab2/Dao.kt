package com.example.lab2
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert(entity = Categories::class)
    fun insertCategories(category: Categories)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Categories>

    @Query("SELECT count(id) FROM categories")
    fun getCountCategories(): Int
}