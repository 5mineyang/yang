package com.mineyang.yang.adapther

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mineyang.yang.adapther.util.ItemViewDelegate
import com.mineyang.yang.adapther.util.ViewHolder

/**
 * https://github.com/hongyangAndroid/baseAdapter
 * Created by zhy on 16/4/9.
 */
class CommonAdapter<T>(protected var context: Context, protected var mLayoutId: Int, data: List<T>?,
                       holderConvert: (holder: ViewHolder, data: T, position: Int, payloads: List<Any>?) -> Unit,
                       holderConvertP: ((holder: ViewHolder, data: T, position: Int, payloads: List<Any>?, parent: ViewGroup) -> Unit)? = null,
                       onItemClick: ((view: View, holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null,
                       onItemLongClick: ((view: View, holder: RecyclerView.ViewHolder, position: Int) -> Boolean?)? = null

) : MultiItemTypeAdapter<T>(context, data, holderConvert, holderConvertP, onItemClick, onItemLongClick) {
    protected var mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)

        addItemViewDelegate(object : ItemViewDelegate<T> {

            override fun getItemViewLayoutId(): Int {
                return mLayoutId
            }

            override fun isForViewType(item: T, position: Int): Boolean {
                return true
            }

            override fun convert(holder: ViewHolder, item: T, position: Int, payloads: List<Any>?) {

            }
        })
    }


    fun setData(data: List<T>) {
        this.datas = data
    }
}
