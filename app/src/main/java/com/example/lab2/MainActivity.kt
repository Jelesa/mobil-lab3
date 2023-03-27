package com.example.lab2

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity(), CoursesAdapter.Listener {

    lateinit var categories: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.categories = findViewById(R.id.recyclerView)
        this.categories.layoutManager = LinearLayoutManager(this)

        var task: GetDataCategories  = GetDataCategories(this)
        task.execute("https://www.themealdb.com/api/json/v1/1/categories.php")

    }

    override fun onCLick(dataItem: DataItem) {
        val intent = Intent(this, RecipesActivity::class.java)
        intent.putExtra("name", dataItem.text)
        startActivity(intent)
    }

    class GetDataCategories(private var activity: MainActivity?) : AsyncTask<String, Void, MutableList<DataItem>>() {
        override fun doInBackground(vararg p0: String): MutableList<DataItem> {
            var resultJSON: String? = null;
            var client: OkHttpClient = OkHttpClient();

            try {
                // Create URL
                val url = URL(p0[0])   // Build request
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
                val categoriesArray = obj.getJSONArray("categories")
                for (i in 0 until categoriesArray.length()) {
                    val categoryInfo = categoriesArray.getJSONObject(i)
                    result.add(DataItem(categoryInfo.getString("idCategory"),
                                        categoryInfo.getString("strCategory"),
                                        categoryInfo.getString("strCategoryThumb")))
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
            this.activity?.categories?.adapter = adapter
        }
    }

}
