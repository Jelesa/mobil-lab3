package com.example.lab2

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class RecipesActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name")

        if (savedInstanceState == null) {
            val bundle = bundleOf("nameRecipes" to name)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<FragmentRecipes>(R.id.fragment_container_view_recipes, args = bundle)
            }
        }

        setContentView(R.layout.activity_recipes)
    }
}