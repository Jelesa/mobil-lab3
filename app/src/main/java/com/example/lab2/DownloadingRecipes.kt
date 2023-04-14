package com.example.lab2

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class DownloadingRecipes(context: Context) {

    private var categories: MutableList<DataItem> = mutableListOf()
    private var idRecipes: MutableList<String> = mutableListOf()
    private var context = context
    private lateinit var database: AppDatabase

    fun downloadingCategories()
    {
        var resultJSON: String? = null;
        var client: OkHttpClient = OkHttpClient();
        this.database = Room.databaseBuilder(
            this.context.applicationContext,
            AppDatabase::class.java, "recipe_database"
        ).build()

        var t = Thread{
            try {
                // Create URL
                val url = URL("https://www.themealdb.com/api/json/v1/1/categories.php")   // Build request
                val request = Request.Builder().url(url).build()
                // Execute request
                val response = client.newCall(request).execute()
                resultJSON = response.body?.string()
            }
            catch(err:Error) {
                print("Error when executing get request: "+err.localizedMessage)
            }

            try {
                val obj = JSONObject(resultJSON)
                val categoriesArray = obj.getJSONArray("categories")
                for (i in 0 until categoriesArray.length()) {
                    val categoryInfo = categoriesArray.getJSONObject(i)
                    this.categories.add(DataItem(categoryInfo.getString("idCategory"),
                        categoryInfo.getString("strCategory"),
                        categoryInfo.getString("strCategoryThumb")))
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

            var tDB = Thread{
                for (i in 0 until this.categories.size) {
                    this.database.getDao().insertCategories(Categories(this.categories[i].id,
                            this.categories[i].text,
                            this.categories[i].src))
                }
            }

            tDB.start()
            tDB.join()
        }

        t.start()
        t.join()
    }

    fun downloadingReciepCategories()
    {
        var resultJSON: String? = null;
        var client: OkHttpClient = OkHttpClient();

        var t = Thread{
            for (i in 0 until this.categories.size)
            {
                try {
                    // Create URL
                    val url = URL("https://www.themealdb.com/api/json/v1/1/filter.php?c=" + this.categories[i].text)   // Build request
                    val request = Request.Builder().url(url).build()
                    // Execute request
                    val response = client.newCall(request).execute()
                    resultJSON = response.body?.string()
                }
                catch(err:Error) {
                    print("Error when executing get request: "+err.localizedMessage)
                }

                var result: MutableList<DataItem>  = mutableListOf()

                try {
                    val obj = JSONObject(resultJSON)
                    val categoriesArray = obj.getJSONArray("meals")
                    for (i in 0 until categoriesArray.length()) {
                        val categoryInfo = categoriesArray.getJSONObject(i)
                        result.add(DataItem(categoryInfo.getString("idMeal"),
                            categoryInfo.getString("strMeal"),
                            categoryInfo.getString("strMealThumb")))
                        this.idRecipes.add(categoryInfo.getString("idMeal"))
                    }
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }

                var tDB = Thread{
                    for (j in 0 until result.size) {
                        this.database.getDao().insertRecipesCategories(
                            RecipesCategories(result[j].id,
                                                        this.categories[i].text,
                                                        result[j].text,
                                                        result[j].src)
                        )
                    }
                }

                tDB.start()
                tDB.join()
            }
        }

        t.start()
        t.join()
    }

    fun downloadingRecieps()
    {
        Thread{
            for (i in 0 until this.idRecipes.size)
            {
                var resultJSON: String? = null;
                var client: OkHttpClient = OkHttpClient();

                try {
                    // Create URL
                    val url = URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + this.idRecipes[i])   // Build request
                    val request = Request.Builder().url(url).build()
                    // Execute request
                    val response = client.newCall(request).execute()
                    resultJSON = response.body?.string()
                }
                catch(err:Error) {
                    print("Error when executing get request: "+err.localizedMessage)
                }

                var result: MutableList<InfoRecipe> = mutableListOf()

                try {
                    val obj = JSONObject(resultJSON)
                    val categoriesArray = obj.getJSONArray("meals")
                    for (i in 0 until categoriesArray.length()) {
                        val categoryInfo = categoriesArray.getJSONObject(i)
                        result.add(InfoRecipe(categoryInfo.getString("strMeal"),
                            categoryInfo.getString("strInstructions"),
                            categoryInfo.getString("strMealThumb")))
                    }
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }

                var t = Thread{
                    for (j in 0 until result.size) {
                        this.database.getDao().insertRecipes(
                                    Recipe(this.idRecipes[i],
                                            result[j].name,
                                            result[j].src,
                                            result[j].instruction)
                        )
                    }
                }

                t.start()
                t.join()
            }
        }.start()
    }
}