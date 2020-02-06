package ru.elron.examplevangogh.utils.vangogh

import android.graphics.Bitmap
import androidx.annotation.DrawableRes

class Container(var vangogh: Vangogh): Runnable {
    var imageUrl: String? = null
    var bitmap: Bitmap? = null

    var error: Int = 0
    var listener: Vangogh.VangoghResultListener? = null
    var to: Any? = null

    /**
     * Ресурс на drawable
     */
    fun withError(@DrawableRes error: Int): Container {
        this.error = error
        return this
    }

    fun to(listener: Vangogh.VangoghResultListener, to: Any? = null) {
        this.listener = listener
        this.to = to
        vangogh.start(this)
    }

    val mainRunnable = Runnable{ listener?.onResult(this) }

    override fun run() {
        if (imageUrl == null) {
            onResult()
        } else {
            bitmap = vangogh.cache.get(imageUrl!!)
            if (bitmap != null)
                onResult()
            else {
                // скачиваем
                vangogh.downloader.start(this)

                // кешируем
                if (imageUrl != null && bitmap != null)
                    vangogh.cache.put(imageUrl!!, bitmap!!)

                onResult()
            }
        }
    }

    private fun onResult() {
        if (vangogh.mainHandler != null)
            vangogh.mainHandler!!.post(mainRunnable)
        else
            mainRunnable.run()
    }

    fun clear() {
        bitmap = null
        listener = null
        to = null
        error = 0
    }

    abstract class IBuilder {
        lateinit var vangogh: Vangogh
        abstract fun newInstance(): Container
        abstract fun release(container: Container)

        fun with(vangogh: Vangogh): IBuilder {
            this.vangogh = vangogh
            return this
        }
    }
}