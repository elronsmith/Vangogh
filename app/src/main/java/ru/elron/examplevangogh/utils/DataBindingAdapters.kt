package ru.elron.examplevangogh.utils

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import ru.elron.examplevangogh.App
import ru.elron.examplevangogh.utils.vangogh.Container
import ru.elron.examplevangogh.utils.vangogh.Vangogh

val TAG = DataBindingAdapters::class.java.simpleName
val IMAGE_LISTENER: Vangogh.VangoghResultListener = object : Vangogh.VangoghResultListener {
    override fun onResult(container: Container) {
        Log.d(TAG, "onResult() ${container.to}")
        Log.d(TAG, "onResult() ${container.bitmap}")
        if (container.bitmap != null) {
            when(container.to) {
                is ImageView -> {
                    (container.to as ImageView).setImageBitmap(container.bitmap)
                }
            }
        }
    }
}

@BindingAdapter("app:imageUrl")
fun ImageView?.imageUrl(imageUrl: String) {
    Log.d(TAG, "imageUrl() $imageUrl")
    App.mainHandlerVangogh.fromUrl(imageUrl)
        .to(IMAGE_LISTENER, this)
}

class DataBindingAdapters
