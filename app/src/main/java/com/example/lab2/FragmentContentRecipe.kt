package com.example.lab2

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class FragmentContentRecipe : Fragment() {

    lateinit var textContent: TextView
    lateinit var imagePhoto: ImageView
    lateinit var textName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.id = getArguments()?.getString("idRecipe").toString()
    }
    lateinit var database: AppDatabase

    lateinit var id: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_content_recipe, container, false)

        this.textName = view.findViewById(R.id.textName)
        this.textContent = view.findViewById(R.id.textRecieps)
        this.imagePhoto = view.findViewById(R.id.photoDishes)

        this.database = activity?.let {
            Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java, "recipe_database"
            ).build()
        }!!

        this.id = getArguments()?.getString("idRecipe").toString()

        if (Network().isNetworkAvailable(activity))
        {
            val recipe = GetDataRecipes(this)
            recipe.execute(this.id)
        }
        else
        {
            var getRecipe = GetDataRecipeDatabase(this)
            getRecipe.execute(this.id)
        }

        return view
    }

    companion object {
        fun newInstance() = FragmentContentRecipe()
    }

    class GetDataRecipes(private var activity: FragmentContentRecipe?) : AsyncTask<String, Void, InfoRecipe>() {
        override fun doInBackground(vararg p0: String): InfoRecipe {
            var resultJSON: String? = null;
            var client: OkHttpClient = OkHttpClient();

            try {
                // Create URL
                val url = URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + p0[0])   // Build request
                val request = Request.Builder().url(url).build()
                // Execute request
                val response = client.newCall(request).execute()
                resultJSON = response.body?.string()
            }
            catch(err:Error) {
                print("Error when executing get request: "+err.localizedMessage)
            }

            var result: InfoRecipe = InfoRecipe("", "", "")

            try {
                val obj = JSONObject(resultJSON)
                val categoriesArray = obj.getJSONArray("meals")
                for (i in 0 until categoriesArray.length()) {
                    val categoryInfo = categoriesArray.getJSONObject(i)
                    result = InfoRecipe(categoryInfo.getString("strMeal"),
                        categoryInfo.getString("strInstructions"),
                        categoryInfo.getString("strMealThumb"))
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result:  InfoRecipe) {
            super.onPostExecute(result)
            this.activity!!.textName.text = result.name
            this.activity!!.textContent.text = result.instruction

            Glide.with(this.activity!!.imagePhoto)
                .load(result.src)
                .thumbnail(Glide.with(this.activity!!.imagePhoto).load(R.drawable.food))
                .fitCenter()
                .into(this.activity!!.imagePhoto);
        }
    }

    class GetDataRecipeDatabase(private var activity: FragmentContentRecipe?): AsyncTask<String, Void, InfoRecipe>()
    {
        override fun doInBackground(vararg params: String): InfoRecipe {
            var recipe: Recipe = Recipe("", "", "", "")
            recipe = this.activity!!.database.getDao().getRecipeById(params[0])

            var result: InfoRecipe = InfoRecipe(recipe.name, recipe.instruction, recipe.src)

            return result
        }

        override fun onPostExecute(result: InfoRecipe) {
            super.onPostExecute(result)

            this.activity!!.textName.text = result.name
            this.activity!!.textContent.text = result.instruction

            Glide.with(this.activity!!.imagePhoto)
                .load(result.src)
                .thumbnail(Glide.with(this.activity!!.imagePhoto).load(R.drawable.food))
                .fitCenter()
                .into(this.activity!!.imagePhoto);
        }
    }
}