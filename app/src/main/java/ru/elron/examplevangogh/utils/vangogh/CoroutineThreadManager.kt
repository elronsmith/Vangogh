package ru.elron.examplevangogh.utils.vangogh

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * За скачивание в другом потоке отвечает корутина
 */
class CoroutineThreadManager : Vangogh.IThreadManager {
    val job = CoroutineScope(Dispatchers.Default)

    override fun post(container: Container) {
        job.launch {
            container.runDownloading()
            withContext(Dispatchers.Main) {
                container.onResult()
            }
        }
    }
}