package com.example.lab2

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class ContentRecipeActivity : AppCompatActivity() {
    lateinit var textContent: TextView
    lateinit var imagePhoto: ImageView
    lateinit var textName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_recipe)

        val id = intent.getStringExtra("name")

        this.textName = findViewById(R.id.textName)
        this.textContent = findViewById(R.id.textRecieps)
        this.imagePhoto = findViewById(R.id.photoDishes)

        val recipe = GetDataRecipes(this)
        recipe.execute(id)
    }

    class GetDataRecipes(private var activity: ContentRecipeActivity?) : AsyncTask<String, Void, InfoRecipe>() {
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

            val getImage = GetImageAsyncTask(this.activity!!.imagePhoto)
            getImage.execute(result.src)
        }
    }
}