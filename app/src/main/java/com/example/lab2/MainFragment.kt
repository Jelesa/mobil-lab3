package com.example.lab2

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL


class MainFragment : Fragment(), CoursesAdapter.Listener {

    lateinit var categories: RecyclerView
    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var bind = inflater.inflate(R.layout.fragment_main, container, false)

        this.categories = bind.findViewById(R.id.recyclerView)
        this.categories.layoutManager = LinearLayoutManager(getActivity())

        var activity = activity

        if (activity != null) {
            this.database = Room.databaseBuilder(
                activity.applicationContext,
                AppDatabase::class.java, "recipe_database"
            ).build()
        }

        if (Network().isNetworkAvailable(getActivity()))
        {
            var download = DownloadDatabaseAsyncTask(this, getActivity())
            download.execute()

            var task: GetDataCategories = GetDataCategories(this)
            task.execute("https://www.themealdb.com/api/json/v1/1/categories.php")
        }
        else
        {
            var getCategories = GetDataCategoriesDatabase(this)
            getCategories.execute()
        }

        return bind
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onCLick(dataItem: DataItem) {
        val intent = Intent(getActivity(), RecipesActivity::class.java)
        intent.putExtra("name", dataItem.text)
        startActivity(intent)
    }

    class GetDataCategories(private var activity: MainFragment?) : AsyncTask<String, Void, MutableList<DataItem>>() {
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

    class DownloadDatabaseAsyncTask(private var activity: MainFragment?, private  var context: Context?): AsyncTask<Void, Void, Void>()
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

    class GetDataCategoriesDatabase(private var activity: MainFragment?): AsyncTask<Void, Void, MutableList<DataItem>>()
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

            val adapter: CoursesAdapter = CoursesAdapter(result, this.activity!!)
            this.activity?.categories?.adapter = adapter
        }
    }
}