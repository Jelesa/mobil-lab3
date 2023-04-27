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

    @Insert(entity = RecipesCategories::class)
    fun insertRecipesCategories(recipesCategories: RecipesCategories)

    @Query(value = "select * from recipes_categories where name_category = :name")
    fun getAllRecipesCategory(name: String? = "Chicken"): List<RecipesCategories>

    @Insert(entity = Recipe::class)
    fun insertRecipes(recipe: Recipe)

    @Query("SELECT * FROM recipes where id = :id")
    fun getRecipeById(id: String): Recipe
}