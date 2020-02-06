package ru.elron.examplevangogh.utils.vangogh

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import java.io.*
import java.util.*

/**
 * кеш:
 * - RAM
 * - файлы
 * - ограничение размера кеша
 */
class FileRamCache(rootFile: File, size: Int = 1000) : Vangogh.ICache {
    var cacheMap: MutableMap<String, Bitmap> = HashMap() // ссылка-изображение
    var dateCacheMap: MutableMap<String, Long> = HashMap() // ссылка-дата

    val fileCache: FileCache

    init {
        fileCache = FileCache(
            rootFile,
            size
        )
    }

    override fun get(id: String): Bitmap? {
        // сначала берем из RAM
        var result: Bitmap? = cacheMap[id]
        if (result == null) { // берем из файла
            result = fileCache.get(id)
            if (result != null) { // добавляем в RAM
                val date = fileCache.getDate(id)
                cacheMap[id] = result
                dateCacheMap[id] = date
            }
        }
        return result
    }

    override fun put(id: String, bitmap: Bitmap) {
        val date = System.currentTimeMillis()
        // сохраняем в RAM
        cacheMap[id] = bitmap
        dateCacheMap[id] = date
        // сохраняем в File
        fileCache.put(id, bitmap)
    }

    /**
     * файловый кеш
     * - максимальное кол-во файлов в кеше
     * - /data/user/0/ru.elron/files/cache_images
     */
    class FileCache(val rootFile: File, val size: Int = 1000) : Vangogh.ICache {
        private val handlerThread: HandlerThread
        private val handler: Handler

        init {
            handlerThread = HandlerThread("VangoghFileCache")
            handlerThread.isDaemon = true // работает до окончания главного потока
            handlerThread.start()
            handler = Handler(handlerThread.looper)
        }

        override fun get(id: String): Bitmap? {
            val fileName = obtainFileNameFromUrl(id) ?: return null
            val file = File(rootFile, fileName)
            return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        }

        override fun put(id: String, bitmap: Bitmap) {
            val fileName = obtainFileNameFromUrl(id) ?: return
            handler.post(
                PutRunnable(
                    File(rootFile, fileName),
                    bitmap,
                    this
                )
            )
        }

        /**
         * возвращает дату сохранения файла
         * @param fromUrl ссылка на изображение
         */
        fun getDate(fromUrl: String): Long {
            val fileName = obtainFileNameFromUrl(fromUrl) ?: return 0
            val file = File(rootFile, fileName)
            return file.lastModified()
        }

        /**
         * преобразует ссылку в путь к изображению
         * @param fromUrl ссылка на изображение
         * @return путь к изображению
         */
        fun obtainFileNameFromUrl(fromUrl: String): String? {
            var folderName: String? = null
            var fileName: String? = null
            try { // получаем имя папки
                var index = fromUrl.indexOf("://")
                if (index < 0) index = 0 else if (index > 0) index += 3
                val folderEndIndex = fromUrl.indexOf("/", index)
                if (folderEndIndex > index) folderName = fromUrl.substring(index, folderEndIndex)
                // получаем имя файла
                val fileBeginIndex = fromUrl.lastIndexOf("/")
                val jpgIndex = fromUrl.indexOf(".jpg", fileBeginIndex)
                if (jpgIndex > 0) { // ".jpg" найден
                    if (jpgIndex > fileBeginIndex) fileName =
                        fromUrl.substring(fileBeginIndex + 1, jpgIndex + 4)
                } else { // ".jpg" не найден
                    val qIndex = fromUrl.indexOf("?", fileBeginIndex)
                    fileName = if (qIndex > 0) { // есть знак "?" , копируем до него
                        fromUrl.substring(fileBeginIndex + 1, qIndex) + ".jpg"
                    } else { // нет знака, копируем всё
                        fromUrl.substring(fileBeginIndex + 1) + ".jpg"
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
            return if (folderName == null || fileName == null) null else folderName + fileName
        }

        /** удаляет лишние изображения */
        fun optimizeFileCache() {
            val list = rootFile.listFiles()
            if (list.size > size) {
                // есть лишние файлы, удаляем
                // находим самый старый файл и удаляем
                var index = 0
                var time: Long = Long.MAX_VALUE
                for (i in 0..list.lastIndex) {
                    val s = list[i].lastModified()
                    if (s < time) {
                        time = s
                        index = i
                    }
                }
                list[index].delete()
                optimizeFileCache()
            }
        }
    }

    /**
     * Создаёт новый файл и сохраняет в него изображение
     */
    class PutRunnable(private val file: File,
                      private val bitmap: Bitmap,
                      private val fileCache: FileCache
    ) : Runnable {
        override fun run() {
            if (file.exists()) file.delete()
            if (!file.exists()) {
                file.parentFile.mkdirs()
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            var stream: OutputStream? = null
            try {
                stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                stream?.close()
            }

            fileCache.optimizeFileCache()
        }
    }

    /** очищает папку с кешем  */
    fun clearCache() {
        cacheMap.clear()
        dateCacheMap.clear()
        deleteRecursive(fileCache.rootFile)
    }

    /** удаляет всю папку  */
    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            val childs = fileOrDirectory.listFiles()
            for (child in childs)
                deleteRecursive(child)
        }
        fileOrDirectory.delete()
    }
}