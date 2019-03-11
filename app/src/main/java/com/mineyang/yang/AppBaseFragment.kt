package com.mineyang.yang

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mineyang.yang.utils.SharedSingleton
import com.mineyang.yang.view.dialog.DialogCustom

/**
 * 父Fragment
 */
abstract class AppBaseFragment : Fragment() {
    private val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    protected val sharedSingleton = SharedSingleton.instance
    protected lateinit var mView: View
    protected lateinit var myDialog: DialogCustom

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(getLayoutId(), container, false)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myDialog = DialogCustom(context)
        initData()
        initView()
        initListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //防止Fragment重叠
        savedInstanceState?.let {
            val isSupportHidden = it.getBoolean(STATE_SAVE_IS_HIDDEN)
            fragmentManager?.apply {
                val ft = beginTransaction()
                if (isSupportHidden) {
                    ft.hide(this@AppBaseFragment)
                } else {
                    ft.show(this@AppBaseFragment)
                }
                ft.commit()
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
        }
    }

    protected abstract fun getLayoutId(): Int

    open fun initData() {}

    open fun initView() {}

    open fun initListener() {}

    override fun onDestroy() {
        super.onDestroy()
        myDialog.unBindContext()
    }
}