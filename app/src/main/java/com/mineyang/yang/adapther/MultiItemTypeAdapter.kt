package com.mineyang.yang.adapther

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.mineyang.yang.adapther.util.ItemViewDelegate
import com.mineyang.yang.adapther.util.ItemViewDelegateManager
import com.mineyang.yang.adapther.util.ViewHolder

/**
 * https://github.com/hongyangAndroid/baseAdapter
 * Created by zhy on 16/4/9.
 */
open class MultiItemTypeAdapter<T>(
        protected var mContext: Context, open var datas: List<T>?,
        var holderConvert: (holder: ViewHolder, data: T, position: Int, payloads: List<Any>?) -> Unit,
        var holderConvertP: ((holder: ViewHolder, data: T, position: Int, payloads: List<Any>?, parent: ViewGroup) -> Unit)? = null,
        var itemClick: ((view: View, holder: RecyclerView.ViewHolder, position: Int) -> Unit?)? = null,
        var itemLongClick: ((view: View, holder: RecyclerView.ViewHolder, position: Int) -> Boolean?)? = null
) : RecyclerView.Adapter<ViewHolder>() {
    protected var mItemViewDelegateManager: ItemViewDelegateManager<T> = ItemViewDelegateManager<T>()
    private var mParent: ViewGroup? = null

    override fun getItemViewType(position: Int): Int {
        return if (!useItemViewDelegateManager() || datas == null) {
            super.getItemViewType(position)
        } else {
            mItemViewDelegateManager.getItemViewType(datas!![position], position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType)
        val layoutId = itemViewDelegate.getItemViewLayoutId()
        val holder = ViewHolder.createViewHolder(mContext, parent, layoutId)
        onViewHolderCreated(parent, holder, holder.convertView)
        setListener(parent, holder, viewType)
        mParent = parent
        return holder
    }

    open fun onViewHolderCreated(parent: ViewGroup, holder: ViewHolder, itemView: View) {

    }

    fun convert(holder: ViewHolder, item: T, position: Int, payloads: List<Any>?, parent: ViewGroup) {
        mItemViewDelegateManager.convert(holder, item, holder.adapterPosition, payloads)
        holderConvert(holder, item, position, payloads)
        holderConvertP?.invoke(holder, item, position, payloads, parent)
    }

    protected fun isEnabled(viewType: Int): Boolean {
        return true
    }


    protected fun setListener(parent: ViewGroup, viewHolder: ViewHolder, viewType: Int) {
        if (!isEnabled(viewType)) {
            return
        }
        viewHolder.convertView.setOnClickListener { v ->
            val position = viewHolder.adapterPosition
            itemClick?.let { it(v, viewHolder, position) }
        }

        viewHolder.convertView.setOnLongClickListener { v ->
            val position = viewHolder.adapterPosition
            itemLongClick?.let { it(v, viewHolder, position) } ?: false
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        convert(holder, datas!![position], position, null, mParent!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            Log.d(TAG, "onBindViewHolder: #$position payloads is can use")
            convert(holder, datas!![position], position, payloads, mParent!!)
        }
    }

    override fun getItemCount(): Int {
        return if (datas == null) 0 else datas!!.size
    }

    fun addItemViewDelegate(itemViewDelegate: ItemViewDelegate<T>): MultiItemTypeAdapter<*> {
        mItemViewDelegateManager.addDelegate(itemViewDelegate)
        return this
    }

    fun addItemViewDelegate(viewType: Int, itemViewDelegate: ItemViewDelegate<T>): MultiItemTypeAdapter<*> {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate)
        return this
    }

    protected fun useItemViewDelegateManager(): Boolean {
        return mItemViewDelegateManager.itemViewDelegateCount > 0
    }

    companion object {
        private val TAG = "MultiItemTypeAdapter"
    }
}
