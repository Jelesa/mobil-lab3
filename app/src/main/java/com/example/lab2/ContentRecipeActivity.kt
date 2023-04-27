package com.example.lab2

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.room.Room
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class ContentRecipeActivity : AppCompatActivity() {
    lateinit var textContent: TextView
    lateinit var imagePhoto: ImageView
    lateinit var textName: TextView
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra("name")

        if (savedInstanceState == null) {
            val bundle = bundleOf("idRecipe" to id)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<FragmentContentRecipe>(R.id.fragment_container_view_content, args = bundle)
            }
        }

        setContentView(R.layout.activity_content_recipe)

    }
}