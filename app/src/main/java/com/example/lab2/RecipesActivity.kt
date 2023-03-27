package com.example.lab2

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val getRecieps = GetDataRecipes(this)
        getRecieps.execute(name)
    }

    override fun onCLick(dataItem: DataItem) {
        val intent = Intent(this, ContentRecipeActivity::class.java)
        intent.putExtra("name", dataItem.id)
        startActivity(intent)
    }

    class GetDataRecipes(private var activity: RecipesActivity?) : AsyncTask<String, Void, MutableList<DataItem>>() {
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
            val adapter: CoursesAdapter = CoursesAdapter(result, this.activity!!)
            this.activity!!.recipes.adapter = adapter
        }
    }

}