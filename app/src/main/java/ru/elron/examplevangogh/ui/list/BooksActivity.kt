package ru.elron.examplevangogh.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_books.*
import ru.elron.examplevangogh.R

/**
 * https://github.com/zazk/Open-Api-Resources
 * - Google Books API
 */
class BooksActivity : AppCompatActivity() {
    companion object {
        private val TAG = BooksActivity::class.java.simpleName
        private val ARG_SEARCH = "search"

        fun start(activity: AppCompatActivity, search: String) {
            activity.startActivity(
                Intent(activity, BooksActivity::class.java)
                    .putExtra(ARG_SEARCH, search))
        }
    }

    lateinit var viewModel: BooksViewModel
    val statusObserver = object : Observer<Int> {
        override fun onChanged(status: Int?) {
            when(status) {
                BooksViewModel.STATUS_EMPTY -> {
                    emptyTextView.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    recyclerView.visibility = View.INVISIBLE
                }
                BooksViewModel.STATUS_LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    emptyTextView.visibility = View.INVISIBLE
                }
                BooksViewModel.STATUS_DATA -> {
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    emptyTextView.visibility = View.INVISIBLE
                }
                BooksViewModel.STATUS_ERROR -> {
                    emptyTextView.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    recyclerView.visibility = View.INVISIBLE
                }
                else -> requestBooks()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)
        viewModel = ViewModelProvider(this).get(BooksViewModel::class.java)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = viewModel.adapter

        viewModel.statusLiveData.observe(this, statusObserver)
    }

    fun requestBooks() {
        val search = intent?.getStringExtra(ARG_SEARCH)
        if (search == null)
            viewModel.statusLiveData.postValue(BooksViewModel.STATUS_EMPTY)
        else {
            viewModel.statusLiveData.postValue(BooksViewModel.STATUS_LOADING)
            viewModel.requestBooksAsync(search)
        }
    }
}
