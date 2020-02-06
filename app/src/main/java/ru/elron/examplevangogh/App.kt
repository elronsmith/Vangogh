package ru.elron.examplevangogh

import android.app.Application
import android.util.Log
import ru.elron.examplevangogh.utils.vangogh.FileRamCache
import ru.elron.examplevangogh.utils.vangogh.MultiThreadManager
import ru.elron.examplevangogh.utils.vangogh.PoolContainerBuilder
import ru.elron.examplevangogh.utils.vangogh.Vangogh
import java.io.File

class App: Application() {

    companion object {
        val TAG = App::class.java.simpleName

        // без cache, без пула
        lateinit var vangogh: Vangogh
        // c кешем
        lateinit var cacheVangogh: Vangogh
        // с кешем, результат возвращается в главном потоке
        lateinit var mainHandlerVangogh: Vangogh
        // c кешем, c пулом и 2 потоками
        lateinit var poolVangogh: Vangogh

    }

    override fun onCreate() {
        super.onCreate()

        val cacheFile = File(filesDir, "cache_images")
        Log.d(TAG, "onCreate() ${cacheFile.absolutePath}")

        vangogh = Vangogh.Builder()
//            .withDownloader(OkHttp3ImageDownloader())
            .build()
        cacheVangogh = Vangogh.Builder()
            .withCache(FileRamCache(cacheFile))
            .build()
        mainHandlerVangogh = Vangogh.Builder()
            .withCache(FileRamCache(cacheFile))
            .withMainHandler()
            .build()
        poolVangogh = Vangogh.Builder()
            .withCache(FileRamCache(cacheFile))
            .withContainerBuilder(PoolContainerBuilder())
            .withThreadManager(MultiThreadManager())
            .build()

    }
}
