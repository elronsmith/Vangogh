package ru.elron.examplevangogh.utils.vangogh

import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL

class DefaultImageDownloader: Vangogh.IDownloader {
    override fun start(container: Container) {
        try {
            val url = URL(container.imageUrl)

            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            container.bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            container.bitmap = null
        }
    }
}