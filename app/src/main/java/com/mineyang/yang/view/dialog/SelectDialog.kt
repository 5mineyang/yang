//package com.mineyang.yang.view.dialog
//
//import android.app.Dialog
//import android.content.Context
//import android.os.Bundle
//import android.support.v7.widget.GridLayoutManager
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.view.Gravity
//import android.view.View
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import android.widget.TextView
//import com.mineyang.yang.R
//import com.mineyang.yang.adapther.CommonAdapter
//import com.mineyang.yang.utils.dp2px
//
///**
// *  Description :从下往上弹出的dialog
//
// *  Author:yang
//
// *  Email:1318392199@qq.com
//
// *  Date: 2019/1/4
// */
//@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
//class SelectDialog constructor(context: Context, private val menuList: ArrayList<SelectBean>,
//                               val isDefaultLayout: Boolean = true, val onItemClick: (position: Int) -> Unit = { },
//                               val onCancelClick: (view: View, menuList: ArrayList<SelectBean>) -> Unit = { _, _ -> },
//                               theme: Int = R.style.dialog_common) : Dialog(context, theme) {
//    private lateinit var mDialogView: View                          //dialog
//    private lateinit var mTvCancel: TextView                        //取消按钮
//    private lateinit var mLayoutManager: LinearLayoutManager        //布局管理器
//    private lateinit var mmGridLayoutManager: GridLayoutManager     //布局管理器
//    private lateinit var mRvSelect: RecyclerView                    //菜单列表
//    private lateinit var mAdapter: CommonAdapter<SelectBean>        //适配器
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mDialogView = View.inflate(context, R.layout.layout_select_dialog, null)
//        setContentView(mDialogView)
//        initView()
//        initListener()
//    }
//
//    private fun initView() {
//        mTvCancel = mDialogView.findViewById(R.id.tvCancel)
//        mRvSelect = mDialogView.findViewById(R.id.rvSelect)
//
//        val wlp = window.attributes
//        //设置位置和动画
//        wlp.gravity = Gravity.BOTTOM
//        wlp.windowAnimations = R.style.bottomDialog_animStyle
//        window.attributes = wlp
//
//        if (isDefaultLayout) {
//            mRvSelect.background = null
//        } else {
//            mTvCancel.text = "确定"
//            mRvSelect.setPadding(context.dp2px(13), context.dp2px(10), context.dp2px(13), context.dp2px(10))
//            mRvSelect.setBackgroundResource(R.drawable.ripple_bg_drawable_white_radius15)
//        }
//
//        initAdapter()
//    }
//
//    private fun initAdapter() {
//        //默认竖向排布
//        if (isDefaultLayout) {
//            mLayoutManager = LinearLayoutManager(context)
//            mRvSelect.layoutManager = mLayoutManager
//            mAdapter = CommonAdapter(context, R.layout.layout_select_dialog_item, menuList, holderConvert = { holder, t, position, _ ->
//                holder.apply {
//                    //如果不止一个
//                    if (menuList.size > 1) {
//                        //按位置设置背景
//                        when (position) {
//                            0 -> {
//                                getView<View>(R.id.viewSelectDialogRvLine).visibility = View.GONE
//                                getView<TextView>(R.id.tvSelectDialogRvMenuL).setBackgroundResource(R.drawable.ripple_bg_white_top_radius15)
//                            }
//                            menuList.lastIndex -> {
//                                getView<View>(R.id.viewSelectDialogRvLine).visibility = View.VISIBLE
//                                getView<TextView>(R.id.tvSelectDialogRvMenuL).setBackgroundResource(R.drawable.ripple_bg_white_bottom_radius15)
//                            }
//                            else -> {
//                                getView<View>(R.id.viewSelectDialogRvLine).visibility = View.VISIBLE
//                                getView<TextView>(R.id.tvSelectDialogRvMenuL).setBackgroundResource(R.drawable.ripple_bg_white)
//                            }
//                        }
////                        //设置点击状态
////                        getView<TextView>(R.id.tvSelectDialogRvMenuCLickL).apply {
////                            if (t.checkState) {
////                                //按位置分别设置点击状态
////                                if (position == 0) {
////                                    setBackgroundResource(R.drawable.ripple_bg_drawable_white_top_radius15_click)
////                                } else if (position == menuList.lastIndex) {
////                                    setBackgroundResource(R.drawable.ripple_bg_drawable_white_bottom_radius15_click)
////                                } else {
////                                    setBackgroundResource(R.drawable.ripple_bg_drawable_white_click)
////                                }
////                            } else {
////                                background = null
////                            }
////                        }
//                    } else {    //只有一个菜单
//                        getView<View>(R.id.viewSelectDialogRvLine).visibility = View.GONE
//                        getView<TextView>(R.id.tvSelectDialogRvMenuL).setBackgroundResource(R.drawable.ripple_bg_white_radius15)
////                        //设置点击状态
////                        getView<TextView>(R.id.tvSelectDialogRvMenuCLickL).apply {
////                            if (t.checkState) {
////                                setBackgroundResource(R.drawable.ripple_bg_drawable_white_radius15_click)
////                            } else {
////                                background = null
////                            }
////                        }
//                    }
//                    setText(R.id.tvSelectDialogRvMenuL, t.name)
//                }
//            }, onItemClick = { _, _, position ->
//                //点击把下标传过去
//                onItemClick(position)
//                dismiss()
//            })
//        } else {  //表格排布
//            mmGridLayoutManager = GridLayoutManager(context, 3)
//            mRvSelect.layoutManager = mmGridLayoutManager
//            mAdapter = CommonAdapter(context, R.layout.layout_choose_dialog_item, menuList, holderConvert = { holder, t, _, _ ->
//                holder.apply {
//                    getView<RelativeLayout>(R.id.rlSelectDialogRvMenuG).apply {
//                        if (t.checkState) {
//                            setBackgroundColor(resources.getColor(R.color.colorBlueBright))
//                            getView<ImageView>(R.id.ivSelectDialogRvMenuG).visibility = View.VISIBLE
//                        } else {
//                            setBackgroundResource(R.color.color_f8f6f6)
//                            getView<ImageView>(R.id.ivSelectDialogRvMenuG).visibility = View.GONE
//                        }
//                    }
//                    setText(R.id.tvSelectDialogRvMenuG, t.name)
//                }
//            }, onItemClick = { _, _, position ->
//                menuList[position].let {
//                    it.checkState = !it.checkState
//                }
//                mAdapter.notifyDataSetChanged()
//            })
//        }
//        mRvSelect.adapter = mAdapter
//    }
//
//    private fun initListener() {
//        //取消按钮
//        mTvCancel.setOnClickListener {
//            onCancelClick(it, menuList)
//            dismiss()
//        }
//    }
//}