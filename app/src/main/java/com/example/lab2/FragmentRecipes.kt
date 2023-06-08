package com.example.lab2

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
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

class FragmentRecipes : Fragment(), CoursesAdapter.Listener {

    lateinit var recipes: RecyclerView
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //this.name = getArguments()?.getString("nameRecipes").toString()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_recipes, container, false)
        this.recipes = view.findViewById(R.id.recyclerViewRecipers)
        return view
    }

    fun mydata()
    {
        val name = arguments?.getString("nameRecipes").toString()

        this.recipes.layoutManager = LinearLayoutManager(activity)

        this.database = activity?.let {
            Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java, "recipe_database"
            ).build()
        }!!

        if (Network().isNetworkAvailable(activity))
        {
            var task: GetDataRecipes = GetDataRecipes(this)
            task.execute(name)
        }
        else
        {
            var getRecipes = GetDataRecipesCategoriesDatabase(this)
            getRecipes.execute(name)
        }
    }

    companion object {
        fun newInstance() =  FragmentRecipes()
    }

    override fun onCLick(dataItem: DataItem) {
        val intent = Intent(activity, ContentRecipeActivity::class.java)
        intent.putExtra("name", dataItem.id)
        startActivity(intent)
    }


    class GetDataRecipes(private var activity: FragmentRecipes?) : AsyncTask<String, Void, MutableList<DataItem>>() {
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

    class GetDataRecipesCategoriesDatabase(private var activity: FragmentRecipes?): AsyncTask<String, Void, MutableList<DataItem>>()
    {
        override fun doInBackground(vararg params: String): MutableList<DataItem> {
            var recipesCategoriesList: List<RecipesCategories> = listOf()
            recipesCategoriesList = this.activity!!.database.getDao().getAllRecipesCategory(params[0])

            var result: MutableList<DataItem> = mutableListOf()

            for (i in 0 until recipesCategoriesList.size)
            {
                result.add(DataItem(recipesCategoriesList[i].id, recipesCategoriesList[i].nameRecipe, recipesCategoriesList[i].src))
            }

            return result
        }

        override fun onPostExecute(result: MutableList<DataItem>) {
            super.onPostExecute(result)

            val adapter: CoursesAdapter = CoursesAdapter(result, this.activity!!)
            this.activity?.recipes?.adapter = adapter
        }
    }
}