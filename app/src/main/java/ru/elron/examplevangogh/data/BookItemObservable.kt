package ru.elron.examplevangogh.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import ru.elron.examplevangogh.R
import ru.elron.examplevangogh.databinding.ItemBookBinding
import ru.elron.examplevangogh.view.AObservable
import ru.elron.examplevangogh.view.ClickableViewHolder
import ru.elron.examplevangogh.view.OnItemClickViewHolderCallback
import ru.elron.examplevangogh.view.ViewHolderBuilder

class BookItemObservable(val title: String, val description: String, val imageUrl: String) :
        AObservable(ViewHolder.ID)

fun addViewHolder(builderArray: SparseArrayCompat<ViewHolderBuilder>, callback: OnItemClickViewHolderCallback) {
    builderArray.put(ViewHolder.ID, object: ViewHolderBuilder {
        override fun create(parent: ViewGroup): ru.elron.examplevangogh.view.ViewHolder<*> {
            return ViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                callback)
        }
    })
}

class ViewHolder(val binding: ItemBookBinding, callback: OnItemClickViewHolderCallback) : ClickableViewHolder(binding.root, callback) {
    companion object {
        val ID = R.layout.item_book
    }

    override fun update(position: Int) {
        val o = callback.getObservable(position) as BookItemObservable

        binding.observable = o
        binding.root.setOnClickListener(this)
    }
}
