package ru.elron.examplevangogh.utils.vangogh

import android.graphics.Bitmap

class NoCache: Vangogh.ICache {
    override fun get(id: String): Bitmap? = null
    override fun put(id: String, bitmap: Bitmap) { }
}