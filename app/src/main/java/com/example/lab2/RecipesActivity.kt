package com.example.lab2

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.lab2.databinding.ActivityRecipesBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class RecipesActivity : AppCompatActivity(){

    lateinit var binding: ActivityRecipesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesBinding.inflate(layoutInflater)

        val name = intent.getStringExtra("name")

        val bundle = Bundle()
        bundle.putString("nameRecipes", name)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_recipes) as FragmentRecipes
        fragment.arguments = bundle
        fragment.mydata()

        setContentView(binding.root)
    }
}