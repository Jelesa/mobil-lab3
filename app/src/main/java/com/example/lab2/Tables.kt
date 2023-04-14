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

@Entity (tableName = "recipes_categories")
data class RecipesCategories(
    @PrimaryKey
    var id: String,

    @ColumnInfo(name="name_category")
    var nameCategory: String,

    @ColumnInfo (name="name_recipe")
    var nameRecipe: String,

    @ColumnInfo (name="srcImage")
    var src: String
)

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    var id: String,

    @ColumnInfo(name="name")
    var name: String,

    @ColumnInfo(name="src")
    var src: String,

    @ColumnInfo(name="instruction")
    var instruction: String
    )