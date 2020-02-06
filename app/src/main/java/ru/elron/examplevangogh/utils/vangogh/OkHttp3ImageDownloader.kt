package ru.elron.examplevangogh.utils.vangogh

import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.elron.examplevangogh.utils.vangogh.Vangogh.IDownloader

class OkHttp3ImageDownloader: IDownloader {
    val headerMap = HashMap<String, String>()

    companion object {
        val client = OkHttpClient()
    }

    init {
        headerMap.put("User-Agent", "Android")
    }

    override fun start(container: Container) {
        try {
            val builder = Request.Builder().url(container.imageUrl!!)

            if (headerMap.size > 0) {
                val keys = headerMap.keys
                for (k in keys)
                    builder.addHeader(k, headerMap[k]!!)
            }

            val request = builder.build()
            val response = client.newCall(request).execute()
            val body = response.body()

            body?.let {
                container.bitmap = BitmapFactory.decodeStream(it.byteStream())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}