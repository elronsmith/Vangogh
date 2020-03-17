package ru.elron.examplevangogh.utils.vangogh

import android.os.Handler
import android.os.HandlerThread

/**
 * Будет скачивать изображения в одном потоке
 */
class SingleThreadManager(threadName: String = "SingleVangogh") : Vangogh.IThreadManager {
    private val thread: HandlerThread
    private val threadHandler: Handler

    init {
        thread = HandlerThread(threadName)
        thread.isDaemon = true
        thread.start()

        threadHandler = Handler(thread.looper)
    }

    override fun post(container: Container) {
        threadHandler.post(container)
    }
}
