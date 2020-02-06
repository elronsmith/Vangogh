package ru.elron.examplevangogh.ui.github

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import ru.elron.examplevangogh.App
import ru.elron.examplevangogh.R
import ru.elron.examplevangogh.data.User
import ru.elron.examplevangogh.utils.vangogh.Container
import ru.elron.examplevangogh.utils.vangogh.Vangogh.VangoghResultListener
import java.net.URL

class GithubUserViewModel(application: Application) : AndroidViewModel(application) {
    val avatarLiveData = MutableLiveData<Bitmap>()
    val errorAvatarLiveData = MutableLiveData<Int>()
    var user: User? = null
    val onResultListener = object : VangoghResultListener {
        override fun onResult(container: Container) {
            if (container.bitmap == null && container.error != 0)
                errorAvatarLiveData.postValue(container.error)
            else
                avatarLiveData.postValue(container.bitmap)
        }
    }

    fun requestProfile(login: String?) {
        if (user == null) {
            Thread {
                try {
                    val json = URL("https://api.github.com/users/$login").readText()
                    val o = JSONObject(json)
                    user = User(
                        o.optString("login", ""),
                        o.optString("avatar_url", ""))
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    showAvatar()
                }
            }.start()
        } else {
            showAvatar()
        }
    }

    private fun showAvatar() {
        if (user != null)
            App.cacheVangogh.fromUrl(user!!.avatarUrl)
                .withError(R.drawable.vd_android_grey_24dp)
                .to(onResultListener)
        else
            errorAvatarLiveData.postValue(R.drawable.vd_android_grey_24dp)
    }

}
