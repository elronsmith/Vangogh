package ru.elron.examplevangogh.ui.github

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kotlinx.android.synthetic.main.activity_github_profile.*
import ru.elron.examplevangogh.R
import java.net.URLConnection

class GithubUserActivity : AppCompatActivity() {
    lateinit var viewModel: GithubUserViewModel
    val avatarObserver = Observer<Bitmap> {
        it?.let { avatarImageView.setImageBitmap(it) }
    }
    val errorAvatarObserver = Observer<Int> {
        it?.let { avatarImageView.setImageResource(it) }
    }

    companion object {
        private val TAG = GithubUserActivity::class.java.simpleName
        private val ARG_LOGIN = "login"

        fun start(activity: AppCompatActivity, login: String) {
            activity.startActivity(Intent(activity, GithubUserActivity::class.java)
                .putExtra(ARG_LOGIN, login))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_profile)
        viewModel = ViewModelProvider(this).get(GithubUserViewModel::class.java)
        Log.d(TAG, "onCreate() $viewModel")

        val login = intent?.getStringExtra(ARG_LOGIN)
        Log.d(TAG, "onCreate() $login")
        loginTextView.text = login

        if (viewModel.avatarLiveData.value == null)
            viewModel.requestProfile(login)

        viewModel.avatarLiveData.observe(this, avatarObserver)
        viewModel.errorAvatarLiveData.observe(this, errorAvatarObserver)
    }
}
