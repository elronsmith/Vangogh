package ru.elron.examplevangogh.ui.list

import android.app.Application
import android.os.Handler
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import ru.elron.examplevangogh.data.BookItemObservable
import ru.elron.examplevangogh.data.addViewHolder
import ru.elron.examplevangogh.view.AObservable
import ru.elron.examplevangogh.view.OnItemClickViewHolderCallback
import ru.elron.examplevangogh.view.RecyclerAdapter
import java.net.URL

class BooksViewModel : AndroidViewModel, OnItemClickViewHolderCallback {
    val adapter = RecyclerAdapter<BookItemObservable>()
    val statusLiveData = MutableLiveData<Int>(null)
    val mainHandler = Handler()

    companion object {
        val STATUS_EMPTY    = 0
        val STATUS_LOADING  = 1
        val STATUS_DATA     = 2
        val STATUS_ERROR    = 3
    }

    constructor(application: Application) : super(application) {
        addViewHolder(adapter.holderBuilderArray, this)
    }

    override fun onItemClick(v: View?, observable: AObservable, position: Int) { }

    override fun getObservable(position: Int): AObservable = adapter.observableList[position]

    fun requestBooksAsync(search: String) {
        Thread {
            val list: List<BookItemObservable>
            try {
                val json = URL("https://www.googleapis.com/books/v1/volumes?q=$search").readText()
                list = parseListFromJson(json)
                mainHandler.post {
                    addList(list)
                }
                statusLiveData.postValue(if (list.isEmpty()) STATUS_EMPTY else STATUS_DATA)
            } catch (e: Exception) {
                e.printStackTrace()
                statusLiveData.postValue(STATUS_ERROR)
            }
        }.start()
    }

    private fun parseListFromJson(json: String): ArrayList<BookItemObservable> {
        val list = ArrayList<BookItemObservable>()
        val root = JSONObject(json)
        val items = root.optJSONArray("items")
        val size = items.length()
        for (i in 0 until size) {
            val item = items.getJSONObject(i)
            val volumeInfo = item.getJSONObject("volumeInfo")
            volumeInfo?.let {
                val title: String = volumeInfo.optString("title", "нет заголовка")
                val description: String = volumeInfo.optString("description", "нет описания")
                val imageLinks = volumeInfo.optJSONObject("imageLinks")
                val thumbnail: String = imageLinks?.optString("thumbnail") ?: ""

                list.add(BookItemObservable(title, description, thumbnail))
            }
        }

        return list
    }

    private fun addList(list: ArrayList<BookItemObservable>) {
        if (list.isEmpty())
            adapter.observableList.clear()
        else
            adapter.observableList = list

        adapter.notifyDataSetChanged()
    }
}
