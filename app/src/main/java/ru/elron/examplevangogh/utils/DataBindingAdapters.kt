package ru.elron.examplevangogh.utils

import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import ru.elron.examplevangogh.App
import ru.elron.examplevangogh.R
import ru.elron.examplevangogh.utils.vangogh.Container
import ru.elron.examplevangogh.utils.vangogh.Vangogh

val TAG = DataBindingAdapters::class.java.simpleName
val IMAGE_LISTENER: Vangogh.ResultListener = object : Vangogh.ResultListener {
    override fun onResult(container: Container) {
        Log.d(TAG, "onResult() ${container.to} ${container.bitmap}")
        Log.d(TAG, "onResult() ${container.imageUrl}")
        val view = container.to
        if (view is ImageView) {
            if (container.bitmap != null)
                view.setImageBitmap(container.bitmap)
            else if (container.error != 0)
                view.setImageDrawable(ContextCompat.getDrawable(view.context, container.error))
        }
    }
}

@BindingAdapter("app:imageUrl")
fun ImageView?.imageUrl(imageUrl: String) {
    App.coroutinePoolVangogh.fromUrl(imageUrl)
        .withError(R.drawable.vd_android_grey_24dp)
        .to(IMAGE_LISTENER, this)
}

class DataBindingAdapters
