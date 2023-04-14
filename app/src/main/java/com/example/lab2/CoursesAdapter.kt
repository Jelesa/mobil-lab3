package com.example.lab2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL


class CoursesAdapter(context: Context, datalist: List<DataItem>, listener: Listener) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    private var dataList: List<DataItem> = datalist
    private var listener = listener
    private var context = context
    //private var dataList: List<String> = datalist

    override fun getItemCount(): Int {
        return this.dataList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageItem: ImageView = itemView.findViewById(R.id.imageView)
        val textItem: TextView = itemView.findViewById(R.id.textView)

        fun listenOnClick(dataItem: DataItem, position: Int, listener: Listener) {
            itemView.setOnClickListener {
                listener.onCLick(dataItem);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return CourseViewHolder(layoutInflater.inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.imageItem.setImageResource(R.drawable.food)

        /*val getImage = GetImageAsyncTask(this.context, holder.imageItem)
        getImage.execute(dataList[position].src)*/
        Glide.with(holder.imageItem)
            .load(dataList[position].src)
            .thumbnail(Glide.with(holder.imageItem).load(R.drawable.food))
            .fitCenter()
            .into(holder.imageItem);

        holder.textItem.text = dataList[position].text
        holder.listenOnClick(dataList[position], position, listener)
    }

    interface Listener {
        fun onCLick(dataItem: DataItem)
    }

}