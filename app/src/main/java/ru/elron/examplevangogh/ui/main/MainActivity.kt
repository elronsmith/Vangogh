package ru.elron.examplevangogh.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.elron.examplevangogh.R
import ru.elron.examplevangogh.ui.github.GithubUserActivity
import ru.elron.examplevangogh.ui.list.BooksActivity

/**
 *
 * TODO кеш: добавить ограничение по объему занимаемой памяти в RAM
 * TODO добавить пример чтоб изображение было в форме круга
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener {
            val login = editText1.text.toString()
            if (login.isNotBlank())
                GithubUserActivity.start(this, login)
        }

        button2.setOnClickListener {
            val search = editText2.text.toString()
            if (search.isNotBlank())
                BooksActivity.start(this, search)
        }
    }
}
