package com.example.lab2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "categories")
data class Categories(
    @PrimaryKey
    var id: String,

    @ColumnInfo (name="name")
    var name: String,

    @ColumnInfo (name="srcImage")
    var src: String
)

/*@Entity (tableName = "recipes_categories")
data class RecipesCategories(

)*/