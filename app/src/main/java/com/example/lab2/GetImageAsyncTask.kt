package com.example.lab2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class GetImageAsyncTask(val context: Context, val image: ImageView) : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg p0: String?): Bitmap? {
        var result: Bitmap? = BitmapFactory.decodeResource(context.getResources(), R.drawable.food);
        var client: OkHttpClient = OkHttpClient();

        try {
            // Create URL
            var strUrl = p0[0]
            if (p0[0] != "" && p0[0] != null)
            {
            val url = URL(strUrl)   // Build request
            val request = Request.Builder().url(url).build()
            // Execute request
            val response = client.newCall(request).execute()
            val inputStream = response.body?.byteStream()
            result = BitmapFactory.decodeStream(inputStream)
            }
        }
        catch(err:Error) {
            print("Error when executing get request: "+err.localizedMessage)
        }

        return result
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)

            this.image.setImageBitmap(result)

    }
}