package ru.elron.examplevangogh.utils.vangogh

import android.os.Handler
import android.os.HandlerThread

/**
 * Будет скачивать изображения в нескольких потоках
 */
class MultiThreadManager(count: Int = 2) : Vangogh.IThreadManager {
    private var array: Array<Pair<HandlerThread, Handler>?> = arrayOfNulls(count)
    private var index = 0

    init {
        for (i in 0..array.lastIndex)
            array[i] = createThread(i)
    }

    private fun createThread(index: Int): Pair<HandlerThread, Handler> {
        val thread = HandlerThread("VangoghThread$index")
        thread.isDaemon = true
        thread.start()

        return Pair(thread, Handler(thread.looper))
    }

    private fun getHandler(): Handler {
        if (index >= array.size) index = 0
        return array[index++]!!.second
    }

    override fun post(container: Container) {
        getHandler().post(container)
    }
}
