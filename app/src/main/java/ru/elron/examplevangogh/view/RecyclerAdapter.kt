package ru.elron.examplevangogh.view

import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView

val LIST_EMPTY = listOf<AObservable>()

abstract class AObservable(var layoutId: Int = 0)

interface ViewHolderCallback {
    fun getObservable(position: Int): AObservable
}

interface ViewHolderBuilder {
    fun create(parent: ViewGroup): ViewHolder<*>
}

abstract class ViewHolder<C : ViewHolderCallback>(itemView: View, protected var callback: C)
    : RecyclerView.ViewHolder(itemView) {

    abstract fun update(position: Int)
}

interface OnItemClickViewHolderCallback : ViewHolderCallback {
    fun onItemClick(v: View?, observable: AObservable, position: Int)
}

abstract class ClickableViewHolder(itemView: View, callback: OnItemClickViewHolderCallback)
    : ViewHolder<OnItemClickViewHolderCallback>(itemView, callback), View.OnClickListener {

    override fun onClick(v: View) = callback.onItemClick(v, callback.getObservable(adapterPosition), adapterPosition)
}

interface OnLongItemClickViewHolderCallback : OnItemClickViewHolderCallback {
    fun onLongItemClick(v: View?, observable: AObservable, position: Int)
}

abstract class LongClickableViewHolder(itemView: View, callback: OnLongItemClickViewHolderCallback)
    : ViewHolder<OnLongItemClickViewHolderCallback>(itemView, callback), View.OnClickListener, OnLongClickListener {
    override fun onClick(v: View) = callback.onItemClick(v, callback.getObservable(adapterPosition), adapterPosition)

    override fun onLongClick(v: View): Boolean {
        callback.onLongItemClick(v, callback.getObservable(adapterPosition), adapterPosition)
        return true
    }
}

abstract class ARecyclerAdapter : RecyclerView.Adapter<ViewHolder<*>>() {
    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) = holder.update(position)
}

open class RecyclerAdapter<T: AObservable>(
    public var observableList: ArrayList<T> = ArrayList<T>(),
    public var holderBuilderArray: SparseArrayCompat<ViewHolderBuilder> = SparseArrayCompat<ViewHolderBuilder>())
    : ARecyclerAdapter() {

    override fun getItemViewType(position: Int): Int = observableList[position].layoutId
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<*> {
        // если NullPointerException, то нужно в Observable добавить this.type = ViewHolder.TYPE
        return holderBuilderArray.get(viewType)!!.create(parent)
    }
    override fun getItemCount(): Int = observableList.size
}