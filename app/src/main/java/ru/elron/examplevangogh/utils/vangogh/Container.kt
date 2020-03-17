package ru.elron.examplevangogh.utils.vangogh

import android.graphics.Bitmap
import androidx.annotation.DrawableRes

class Container(var vangogh: Vangogh): Runnable {
    var imageUrl: String? = null
    var bitmap: Bitmap? = null

    @DrawableRes
    var error: Int = 0
    var resultListener: Vangogh.ResultListener? = null
    var to: Any? = null

    val mainRunnable = Runnable{ onResult() }

    /**
     * Ресурс на drawable
     */
    fun withError(@DrawableRes error: Int): Container {
        this.error = error
        return this
    }

    fun to(resultListener: Vangogh.ResultListener, to: Any? = null) {
        this.resultListener = resultListener
        this.to = to
        vangogh.start(this)
    }

    override fun run() = vangogh.run(this)
    fun runDownloading() = vangogh.runDownloading(this)

    fun onResultMainThread() {
        if (vangogh.mainHandler != null)
            vangogh.mainHandler!!.post(mainRunnable)
        else
            mainRunnable.run()
    }

    fun onResult() {
        resultListener?.onResult(this)
        vangogh.containerBuilder.release(this)
    }

    fun clear() {
        imageUrl = null
        bitmap = null
        error = 0
        resultListener = null
        to = null
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