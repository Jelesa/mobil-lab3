package com.example.lab2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.lab2.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), CoursesAdapter.Listener {

    lateinit var binding: ActivityMainBinding
    lateinit var categories: RecyclerView
    lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        this.categories = findViewById(R.id.recyclerView)
        this.categories.layoutManager = LinearLayoutManager(this)

        this.database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "recipe_database"
        ).build()

        if (Network().isNetworkAvailable(this))
        {
            //var database = AppDatabase.getDatabase(this)
            var download = DownloadDatabaseAsyncTask(this, this)
            download.execute()

            var task: GetDataCategories  = GetDataCategories(this, this)
            task.execute("https://www.themealdb.com/api/json/v1/1/categories.php")
        }
        else
        {
            var getCategories = GetDataCategoriesDatabase(this, this)
            getCategories.execute()
        }

    }

    override fun onCLick(dataItem: DataItem) {
        val intent = Intent(this, RecipesActivity::class.java)
        intent.putExtra("name", dataItem.text)
        startActivity(intent)
    }



    class GetDataCategories(private var activity: MainActivity?, private  var context: Context?) : AsyncTask<String, Void, MutableList<DataItem>>() {
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
            val adapter: CoursesAdapter = CoursesAdapter(this.context!!, result, this.activity!!)
            this.activity?.categories?.adapter = adapter
        }
    }

    class DownloadDatabaseAsyncTask(private var activity: MainActivity?, private  var context: Context?): AsyncTask<Void, Void, Void>()
    {
        override fun doInBackground(vararg params: Void?): Void? {
            var tmp = this.activity!!.database.getDao().getCountCategories()
            if (tmp == 0)
            {
                var downloader = DownloadingRecipes(this.context!!)
                downloader.downloadingCategories()
                downloader.downloadingReciepCategories()
                downloader.downloadingRecieps()
            }

            return null
        }
    }

    class GetDataCategoriesDatabase(private var activity: MainActivity?, private  var context: Context?): AsyncTask<Void, Void, MutableList<DataItem>>()
    {
        override fun doInBackground(vararg params: Void?): MutableList<DataItem> {
            var categoriesList: List<Categories> = listOf()

            categoriesList = this.activity!!.database.getDao().getAllCategories()

            var result: MutableList<DataItem> = mutableListOf()

            for (i in 0 until categoriesList.size)
            {
                result.add(DataItem(categoriesList[i].id, categoriesList[i].name, categoriesList[i].src))
            }

            return result
        }

        override fun onPostExecute(result: MutableList<DataItem>) {
            super.onPostExecute(result)

            val adapter: CoursesAdapter = CoursesAdapter(this.context!!, result, this.activity!!)
            this.activity?.categories?.adapter = adapter
        }
    }
}

