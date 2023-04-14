package com.example.lab2

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class RecipesActivity : AppCompatActivity(), CoursesAdapter.Listener {

    lateinit var recipes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        val name = intent.getStringExtra("name")

        this.recipes = findViewById(R.id.recyclerViewRecipers)
        this.recipes.layoutManager = LinearLayoutManager(this)

        var database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "recipe_database"
        ).build()

        if (Network().isNetworkAvailable(this))
        {
            var task: RecipesActivity.GetDataRecipes = RecipesActivity.GetDataRecipes(this, this)
            task.execute(name)
        }
        else
        {
            var recipesCategoriesList: List<RecipesCategories> = listOf()
            var threadGetCategories = Thread {
                recipesCategoriesList = database.getDao().getAllRecipesCategory(name)
            }

            threadGetCategories.start()
            threadGetCategories.join()

            var result: MutableList<DataItem> = mutableListOf()

            for (i in 0 until recipesCategoriesList.size)
            {
                result.add(DataItem(recipesCategoriesList[i].id, recipesCategoriesList[i].nameRecipe, recipesCategoriesList[i].src))
            }

            val adapter: CoursesAdapter = CoursesAdapter(this, result, this)
            recipes.adapter = adapter
        }
    }

    override fun onCLick(dataItem: DataItem) {
        val intent = Intent(this, ContentRecipeActivity::class.java)
        intent.putExtra("name", dataItem.id)
        startActivity(intent)
    }

    class GetDataRecipes(private var activity: RecipesActivity?, private var context: Context) : AsyncTask<String, Void, MutableList<DataItem>>() {
        override fun doInBackground(vararg p0: String): MutableList<DataItem> {
            var resultJSON: String? = null;
            var client: OkHttpClient = OkHttpClient();

            try {
                // Create URL
                val url = URL("https://www.themealdb.com/api/json/v1/1/filter.php?c=" + p0[0])   // Build request
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
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result:  MutableList<DataItem>) {
            super.onPostExecute(result)
            val adapter: CoursesAdapter = CoursesAdapter(this.context!!, result, this.activity!!)
            this.activity!!.recipes.adapter = adapter
        }
    }

}