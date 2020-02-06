package ru.elron.examplevangogh.utils.vangogh

import android.graphics.Bitmap
import android.os.Handler

/**
 * Cкачивает изображение из интернета. Подходит для:
 * - скачивания одной аватарки из профиля
 * - скачивание изображений для списков
 *
 * В: Что если нужно отредактировать изображение
 * О: получаете исходный Bitmap в VangoghResultListener, редактируете его
 *
 * Источник:
 * - интернет
 * - файл
 * Конечный пункт:
 * - файл
 * - ImageView
 *
 * Функционал:
 * - несколько других потоков
 * - выбор downloader'a
 * - кеширование
 * - error
 * - poolable
 * - слушатель завершения
 *
 */
class Vangogh {
    internal lateinit var downloader: IDownloader
    internal lateinit var threadManager: IThreadManager
    internal var mainHandler: Handler? = null
    internal lateinit var cache: ICache
    internal lateinit var containerBuilder: Container.IBuilder

    fun fromUrl(imageUrl: String): Container {
        val container = Container(this)
        container.imageUrl = imageUrl
        container.vangogh = this
        return container
    }

    fun start(container: Container) = threadManager.getHandler().post(container)

    class Builder {
        var downloader: IDownloader? = null
        var threadManager: IThreadManager? = null
        var cache: ICache? = null
        var containerBuilder: Container.IBuilder? = null
        var mainHandler: Handler? = null

        /**
         * Реализация класса который будет скачивать изображениек
         */
        fun withDownloader(downloader: IDownloader): Builder {
            this.downloader = downloader
            return this
        }

        /**
         * Реализация менеджера потоков. В этих потоках будет скачиваться изображение
         */
        fun withThreadManager(threadManager: IThreadManager): Builder {
            this.threadManager = threadManager
            return this
        }

        /**
         * VangoghResultListener.onResult() будет вызываться в основном поке
         */
        fun withMainHandler(): Builder {
            this.mainHandler = Handler()
            return this
        }

        /**
         * Реализация кеша
         */
        fun withCache(cache: ICache): Builder {
            this.cache = cache
            return this
        }

        /**
         * Реализация билдера для класса Container.
         * При каждом запросе экземпляр будет создаваться новый или переиспользоваться старый.
         * Второй вариант предпочтительнее когда загружаются списки изображений.
         */
        fun withContainerBuilder(builder: Container.IBuilder): Builder {
            this.containerBuilder = builder
            return this
        }

        fun build(): Vangogh {
            val vangogh = Vangogh()
            vangogh.downloader = this.downloader ?: DefaultImageDownloader()

            vangogh.threadManager = this.threadManager ?: SingleThreadManager()

            vangogh.mainHandler = this.mainHandler

            vangogh.cache = this.cache ?: NoCache()

            vangogh.containerBuilder = this.containerBuilder?.with(vangogh)
                ?: SingleContainerBuilder(vangogh)

            return vangogh
        }
    }

    interface IDownloader {
        fun start(container: Container)
    }

    interface IThreadManager {
        fun getHandler(): Handler
    }

    interface ICache {
        fun get(id: String): Bitmap?
        fun put(id: String, bitmap: Bitmap)
    }

    interface VangoghResultListener {
        /**
         * вызывается когда изображение получено(или нет)
         * может вызываться как в main потоке так и в другом потоке
         */
        fun onResult(container: Container)
    }
}
