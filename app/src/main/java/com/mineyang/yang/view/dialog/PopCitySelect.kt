//package com.mineyang.yang.view.dialog
//
//import android.app.Activity
//import android.graphics.Point
//import android.graphics.drawable.ColorDrawable
//import android.support.v7.widget.LinearLayoutManager
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import android.widget.PopupWindow
//import android.widget.RelativeLayout
//import com.mineyang.yang.R
//import com.mineyang.yang.adapther.CommonAdapter
//import com.mineyang.yang.base.ApiUtils
//import io.reactivex.Observable
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.layout_drag_pop.view.*
//
///**
// *  Description :个人资料城市选择
//
// *  Author:yang
//
// *  Email:1318392199@qq.com
//
// *  Date: 2019/01/05
// */
//class PopCitySelect : PopupWindow {
//    private var mActivity: Activity
//    private lateinit var mLayoutManager: LinearLayoutManager
//    private lateinit var mAdapter: CommonAdapter<AllCity>
//    private var mAllCityBean = AllCityBean()                         //接口数据
//    private var mList = ArrayList<AllCity>()                         //目前列表用到的数据
//    private var mOneCityList = ArrayList<AllCity>()                  //一级城市（省）
//    private var mTwoCityList = ArrayList<AllCity>()                  //二级城市（市）
//    private var mThreeCityList = ArrayList<AllCity>()                //三级城市（区/县）
//    private var mMenuView: View
//    private var mPointPosition = Point()                             //手指按下坐标
//    private var record = arrayOf(0, 0)                               //存放手指按下坐标和时间戳
//    private var defaultTop = 0                                       //弹框原始距离顶部位置
//    private var mCitySelectCallBack: CitySelectCallBack? = null      //回调
//
//    //构造方法
//    constructor(activity: Activity) : super(activity) {
//        this.mActivity = activity
//        mMenuView = LayoutInflater.from(activity).inflate(R.layout.layout_drag_pop, null)
//
//        initListener()
//        initAdapter()
//
//        //获取缓存数据
//        getCacheData()
//
////        //如果页面的临时数据不为空 就不用请求数组
////        if (MySettingsUserActivity.mAllCityList.isNotEmpty()) {
////            //执行数据分类方法
////            setData(MySettingsUserActivity.mAllCityList)
////        } else {
////            httpLoad()
////        }
//
//        //取消按钮
//        mMenuView.ivMySettingsUserPopClose.setOnClickListener {
//            //销毁弹出框
//            dismiss()
//        }
//
//        //顶部手势监听
//        mMenuView.rlMySettingsUserPop.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    //每次按下时记录坐标
//                    mPointPosition.y = event.rawY.toInt()
//                    record[0] = event.rawY.toInt()
//                    record[1] = System.currentTimeMillis().toInt()
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    //每次重绘都会根据上一次最后触屏的mPointPosition.y坐标算出新移动的值
//                    val dy = event.rawY.toInt() - mPointPosition.y
//                    //变化中的顶部距离
//                    val top = v.top + dy
//                    //获取到layoutParams后改变属性 在设置回去
//                    val layoutParams = v.layoutParams as RelativeLayout.LayoutParams
//                    layoutParams.topMargin = top
//                    v.layoutParams = layoutParams
//                    //记录最后一次移动的位置
//                    mPointPosition.y = event.rawY.toInt()
//                }
//                MotionEvent.ACTION_UP -> {
//                    //先根据时间算 如果在0.5秒内的话且向下拉的话 就直接销毁弹框
//                    if (System.currentTimeMillis().toInt() - record[1] < 500 && event.rawY.toInt() > record[0]) {
//                        dismiss()
//                    } else {    //然后再根据移动距离判断是否销毁弹框
//                        //下移超过200就销毁 否则弹回去
//                        if (event.rawY.toInt() - record[0] > 300) {
//                            dismiss()
//                        } else {
//                            //获取到layoutParams后改变属性 在设置回去
//                            val layoutParams = v.layoutParams as RelativeLayout.LayoutParams
//                            layoutParams.topMargin = defaultTop
//                            v.layoutParams = layoutParams
//                        }
//                    }
//                    mMenuView.rlMySettingsUserPop.setIntercept(false)
//                }
//            }
//            //刷新界面
//            mMenuView.rlMySettingsUserPopAll.invalidate()
//            true
//        }
//
//        //RecyclerView监听
//        mMenuView.rvMySettingsUserPop.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    //每次按下时记录坐标
//                    mPointPosition.y = event.rawY.toInt()
//                    record[0] = event.rawY.toInt()
//                    record[1] = System.currentTimeMillis().toInt()
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    //下拉
//                    if (event.rawY <= record[0]) {
//                        mMenuView.rlMySettingsUserPop.setIntercept(false)
//                    } else {
//                        //滑到顶部了
//                        if (!v.canScrollVertically(-1)) {
//                            mMenuView.rlMySettingsUserPop.setIntercept(true)
//                        }
//                    }
//                }
//            }
//            false
//        }
//
//        //设置SelectPicPopupWindow的View
//        this.contentView = mMenuView
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.width = ViewGroup.LayoutParams.MATCH_PARENT
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.isFocusable = true
//        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.animationStyle = R.style.bottomDialog_animStyle
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(ColorDrawable(-0x00000000))
//
//        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        mMenuView.setOnTouchListener { _, event ->
//            val height = mMenuView.rlMySettingsUserPop.top
//            val y = event.y
//            if (event.action == MotionEvent.ACTION_UP) {
//                if (y < height) {
//                    dismiss()
//                }
//            }
//            true
//        }
//    }
//
//    private fun initListener() {
//
//    }
//
//    private fun initAdapter() {
//        mLayoutManager = LinearLayoutManager(mActivity)
//        mMenuView.rvMySettingsUserPop.layoutManager = mLayoutManager
//        mAdapter = CommonAdapter(mActivity, R.layout.activity_my_settings_user_citypop_item, mList, holderConvert = { holder, t, _, _ ->
//            holder.apply {
//                setText(R.id.tvMySettingsUserPopRvCity, t.name)
//                getView<LinearLayout>(R.id.rlSelectDialogRvMenuL).setOnTouchListener { _, event ->
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            //每次按下时记录坐标
//                            mPointPosition.y = event.rawY.toInt()
//                            record[0] = event.rawY.toInt()
//                            record[1] = System.currentTimeMillis().toInt()
//                        }
//                    }
//                    false
//                }
//            }
//        }, onItemClick = { _, _, position ->
//            //第一次点击
//            if (mMenuView.rlMySettingsUserPopTopBottom.visibility == View.GONE) {
//                mMenuView.rlMySettingsUserPopTopBottom.visibility = View.VISIBLE
//                mMenuView.tvMySettingsUserPopOneTips.text = mList[position].name
//                mMenuView.tvMySettingsUserPopTips.text = "选择城市"
//                //遍历二级城市列表 把所有父id为当前城市id的市拿出来加到mList里去
//                mList.clear()
//                mTwoCityList.forEach {
//                    if (it.fatherId.toString() == mOneCityList[position].id) {
//                        mList.add(it)
//                    }
//                }
//                //如果mList是空的 那么就直接关闭pop 调回调方法
//                if (mList.isEmpty()) {
//                    mCitySelectCallBack?.onCitySelectCallBack(mOneCityList[position])
//                    dismiss()
//                } else {
//                    //滚到第一个
//                    mMenuView.rvMySettingsUserPop.scrollToPosition(0)
//                    mAdapter.notifyDataSetChanged()
//                }
//            } else if (mMenuView.tvMySettingsUserPopTwoDot.visibility == View.GONE) {  //选择第二个城市了
//                mMenuView.tvMySettingsUserPopTwoDot.visibility = View.VISIBLE
//                mMenuView.tvMySettingsUserPopTwoTips.visibility = View.VISIBLE
//                mMenuView.tvMySettingsUserPopTwoTips.text = mList[position].name
//                mMenuView.tvMySettingsUserPopThreeTips.text = "请选择县"
//                mMenuView.tvMySettingsUserPopTips.text = "选择区/县"
//                //先把当前城市保存下来 以便后面比较
//                val city = mList[position]
//                mList.clear()
//                mThreeCityList.forEach {
//                    if (it.fatherId.toString() == city.id) {
//                        mList.add(it)
//                    }
//                }
//                if (mList.isEmpty()) {
//                    mCitySelectCallBack?.onCitySelectCallBack(city)
//                    dismiss()
//                } else {
//                    //滚到第一个
//                    mMenuView.rvMySettingsUserPop.scrollToPosition(0)
//                    mAdapter.notifyDataSetChanged()
//                }
//            } else {  //选择第三个县
//                //调用回调
//                mCitySelectCallBack?.onCitySelectCallBack(mList[position])
//                dismiss()
//            }
//        })
//        mMenuView.rvMySettingsUserPop.adapter = mAdapter
//    }
//
//    //城市列表请求
//    private fun httpLoad(version: String? = null) {
//        ApiUtils.getApi().let {
//            if (version == null || version == "") {
//                it.getCityStatic()
//            } else {
//                it.getCityStatic(version)
//            }
//        }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({
//                    it.apply {
//                        if (code == 12000) {
//                            //数据库先删除bean
//                            BoxUtils.removeAllCity(mAllCityBean)
//                            mAllCityBean = data!!
//                            //数据库保存bean
//                            BoxUtils.saveAllCity(mAllCityBean)
//                            //执行数据分类方法
//                            setData(mAllCityBean)
//                        } else if (code == 20000) {
//
//                        }
//                    }
//                }, {
//
//                }, {}, {})
//    }
//
//    //获取缓存数据
//    private fun getCacheData() {
//        Observable.create<AllCityBean> {
//            mAllCityBean = BoxUtils.getAllCity()!!
////            mAllCityBean.version = mAllCityBean.city[0].version
//            it.onNext(mAllCityBean)
//        }.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    //页面赋值
//                    setData(it)
//                    //请求下接口
//                    httpLoad(it.version)
//                }, {
//                    httpLoad()
//                })
//    }
//
//    //数据分类
//    private fun setData(bean: AllCityBean) {
//        mOneCityList.clear()
//        mTwoCityList.clear()
//        mThreeCityList.clear()
//        //遍历请求到的数组 然后一个个分类存放
//        bean.city.forEach {
//            // || it.level == 0
//            if (it.level == 1) {
//                mOneCityList.add(it)
//                //先从省级别选择
//                mList.add(it)
//            } else if (it.level == 2) {
//                mTwoCityList.add(it)
//            } else if (it.level == 3) {
//                mThreeCityList.add(it)
//            }
//        }
//        mMenuView.pbYingyingRecommend.visibility = View.GONE
//        mMenuView.tvMySettingsUserPopTips.visibility = View.VISIBLE
//        mAdapter.notifyDataSetChanged()
//    }
//
//    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
//        super.showAtLocation(parent, gravity, x, y)
//        backgroundAlphaExt(0.5f)
//    }
//
//    override fun dismiss() {
//        super.dismiss()
//        backgroundAlphaExt(1f)
//    }
//
//    //改变背景亮度
//    private fun backgroundAlphaExt(bgAlpha: Float) {
//        val lp = mActivity.window.attributes
//        //0.0-1.0
//        lp?.alpha = bgAlpha
//        mActivity.window.attributes = lp
//    }
//
//    //回调方法
//    fun setOnCitySelectListener(citySelectListener: CitySelectCallBack) {
//        mCitySelectCallBack = citySelectListener
//    }
//
//    interface CitySelectCallBack {
//        fun onCitySelectCallBack(city: AllCity)
//    }
//}