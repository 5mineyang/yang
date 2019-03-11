package com.youke.yingba.base.utils

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.os.Handler
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView

/**
 * 缩放ImageView
 *
 * @author wanglu
 */
class ZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0,
                                              var onItemClick: (() -> Unit)? = null, var onItemLongClick: (() -> Boolean?)? = null) : ImageView(context, attrs, defStyle), ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private var mOnce = false

    private var mInitScale: Float = 0.toFloat()
    private var mMidScale: Float = 0.toFloat()
    private var mMaxScale: Float = 0.toFloat()
    private val mMatrix: Matrix = Matrix()
    private val mScaleGestureDetector: ScaleGestureDetector//捕获用户多指触控时缩放的比例
    private var mFirstClickTime: Long = 0    //单击时间
    private var mSecondClickTime: Long = 0   //第二次时间
    private var mLongPoint = Point(0, 0)      //长按可微偏移坐标

    //---------------------自由移动的变量------------------------
    /**
     * 记录上次多点触控的数量
     */
    private var mLastPointerCount: Int = 0
    //记录上次中心点的坐标
    private var mLastPointerX: Float = 0.toFloat()
    private var mLastPointerY: Float = 0.toFloat()

    /**
     * 系统触发的最小滑动距离
     */
    private val mTouchSlop: Float

    private var isCanDrag: Boolean = false

    private var isCheckLeftAndRight: Boolean = false
    private var isCheckTopAndBottom: Boolean = false

    //---------------------双击放大与缩小变量-------------------------
    private val mGestureDetector: GestureDetector//用户双击手势的对象变量
    private var isScaling: Boolean = false//是否正在放大或缩小---防止用户在正在放大或缩小时疯狂点击

    /**
     * 获取图片当前的缩放值
     *
     * @return
     */
    val scale: Float
        get() {
            val values = FloatArray(9)
            mMatrix.getValues(values)
            return values[Matrix.MSCALE_X]
        }

    /**
     * 获得图片放大或缩小之后的宽和高 以及 left top right bottom的坐标点
     *
     * @return
     */
    private val matrixRectF: RectF
        get() {
            val matrix = mMatrix
            val rect = RectF()
            val drawable = drawable
            if (null != drawable) {
                rect.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
                matrix.mapRect(rect)
            }
            return rect
        }

    private inner class SlowlyScaleRunnable(//缩放的目标值
            private val mTargetScale: Float, //缩放的中心点
            private val x: Float, private val y: Float) : Runnable {

        //放大与缩小的梯度
        private val BEGGER = 1.1f
        private val SMALL = 0.85f

        private var tmpScale: Float = 0.toFloat()

        init {
            if (scale < mTargetScale) {
                tmpScale = BEGGER
            }
            if (scale > mTargetScale) {
                tmpScale = SMALL
            }
        }

        override fun run() {
            //进行缩放
            mMatrix.postScale(tmpScale, tmpScale, x, y)
            checkBorderAndCenterWhenScale()
            imageMatrix = mMatrix
            val currentScale = scale
            if (tmpScale > 1.0f && currentScale < mTargetScale || tmpScale < 1.0f && currentScale > mTargetScale) {
                postDelayed(this, 16)
            } else {
                isScaling = false
                //到达了目标值
                val scale = mTargetScale / currentScale
                mMatrix.postScale(scale, scale, x, y)
                checkBorderAndCenterWhenScale()
                imageMatrix = mMatrix
            }
        }
    }

    init {
        // 初始化
        scaleType = ImageView.ScaleType.MATRIX

        mScaleGestureDetector = ScaleGestureDetector(context, this)
        this.setOnClickListener {
            //0.2秒后触发单机事件 为了和双击屏幕辨别开来
            Handler().postDelayed({
                //如果手指松开的时间在按下之后的0.1秒内 就可以触发
                if (!isScaling && (mSecondClickTime - mFirstClickTime in 1..100)) {
                    onItemClick?.let { it() }
                }
            },200)
        }
        this.setOnLongClickListener {
            //只有onTouch没有移动时才可以触发长按
            if (!isScaling) {
                onItemLongClick?.let { it() } ?: false
            }
            !isScaling
        }
        setOnTouchListener(this)
        //系统触发的最小滑动距离
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()

        //双击放大与缩小
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (isScaling) {
                    return true
                }
                //以此点为缩放中心
                val x = e.x
                val y = e.y

                if (scale < mMidScale) {
                    postDelayed(SlowlyScaleRunnable(mMidScale, x, y), 16)
                    isScaling = true
                    //					mMatrix.postScale(mMidScale/getScale(), mMidScale/getScale(), x, y);
                    //					checkBorderAndCenterWhenScale();
                    //					setImageMatrix(mMatrix);
                } else {
                    postDelayed(SlowlyScaleRunnable(mInitScale, x, y), 16)
                    isScaling = true
                    //					mMatrix.postScale(mInitScale/getScale(), mInitScale/getScale(), x, y);
                    //					checkBorderAndCenterWhenScale();
                    //					setImageMatrix(mMatrix);
                }
                return true
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //注册onGlobalLayoutListener
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //移除onGlobalLayoutListener
        viewTreeObserver.removeGlobalOnLayoutListener(this)
    }

    /**
     * 捕获图片加载完成事件 onMeasure 和onDraw都不适合
     */
    override fun onGlobalLayout() {
        //初始化的操作 一次就好  为了保证对缩放只进行一次
        if (!mOnce) {

            //得到控件的宽和高--不一定是屏幕的宽和高 可能会有actionBar等等
            val width = width
            val height = height

            //得到我们的图片 以及宽和高
            val drawable = drawable ?: return
            /**
             * 这里说下Drawable这个抽象类，具体实现类为BitmapDrawable
             * BitmapDrawable这个类重写了getIntrinsicWidth()和getIntrinsicHeight()方法
             * 这个两个方法看字面意思就知道是什么了，就是得到图片固有的宽和高的
             */
            val intrinsicWidth = drawable.intrinsicWidth
            val intrinsicHeight = drawable.intrinsicHeight
            //            Log.e("SCALE_IMAGEVIEW", intrinsicWidth+":intrinsicWidth");
            //            Log.e("SCALE_IMAGEVIEW", intrinsicHeight+":intrinsicHeight");
            // 如果图片宽度比控件宽度小  高度比控件大 需要缩小
            var scale = 1.0f
            if (width > intrinsicWidth && height < intrinsicHeight) {
                scale = height * 1.0f / intrinsicHeight
            }
            // 如果图片比控件大 需要缩小
            if (width < intrinsicWidth && height > intrinsicHeight) {
                scale = width * 1.0f / intrinsicWidth
            }

            if (width < intrinsicWidth && height < intrinsicHeight || width > intrinsicWidth && height > intrinsicHeight) {
                scale = Math.min(width * 1.0f / intrinsicWidth, height * 1.0f / intrinsicHeight)
            }

            /**
             * 得到初始化缩放的比例
             */
            mInitScale = scale
            mMidScale = 2 * mInitScale//双击放大的值
            mMaxScale = 64 * mInitScale//放大的最大值

            //将图片移动到控件的中心
            val dx = width / 2 - intrinsicWidth / 2
            val dy = height / 2 - intrinsicHeight / 2
            //将一些参数设置到图片或控件上 设置平移缩放 旋转
            mMatrix.postTranslate(dx.toFloat(), dy.toFloat())
            mMatrix.postScale(mInitScale, mInitScale, (width / 2).toFloat(), (height / 2).toFloat())//以控件的中心进行缩放
            imageMatrix = mMatrix

            mOnce = true
        }
    }

    //缩放区间 initScale --- maxScale
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scale = scale
        //捕获用户多指触控时缩放的比例
        var scaleFactor = detector.scaleFactor
        //        Log.e("ScaleGestrueDetector", "scaleFactor:"+scaleFactor);
        if (drawable == null) {
            return true
        }
        //最大最小控制
        if (scale < mMaxScale && scaleFactor > 1.0f || scale > mInitScale && scaleFactor < 1.0f) {
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale
            }
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale
            }

            mMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            //不断检测 控制白边和中心位置
            checkBorderAndCenterWhenScale()
            imageMatrix = mMatrix
        }
        return true
    }

    /**
     * 在缩放的时候进行边界控制以及我们的中心位置控制
     */
    private fun checkBorderAndCenterWhenScale() {
        val rect = matrixRectF
        var delatX = 0f
        var delatY = 0f
        //控件的宽和高
        val width = width
        val height = height
        //        Log.i("top", "top:"+rect.top);
        //        Log.i("left", "left:"+rect.left);
        //        Log.i("right", "right:"+rect.right);
        //        Log.i("bottom", "bottom:"+rect.bottom);

        //如果图片的宽和高大于控件的宽和高 在缩放过程中会产生border 进行偏移补偿
        if (rect.width() >= width) {
            if (rect.left > 0) {
                delatX = -rect.left
            }
            if (rect.right < width) {
                delatX = width - rect.right
            }
        }

        if (rect.height() >= height) {
            if (rect.top > 0) {
                delatY = -rect.top
            }
            if (rect.bottom < height) {
                delatY = height - rect.bottom
            }
        }

        //如果图片的宽和高小于控件的宽和高 让其居中
        if (rect.width() < width) {
            delatX = width / 2 - rect.right + rect.width() / 2f
        }
        if (rect.height() < height) {
            delatY = height / 2 - rect.bottom + rect.height() / 2f
        }
        mMatrix.postTranslate(delatX, delatY)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        //        Log.e("ScaleGestrueDetector", "onScaleBegin");
        return true//修改为true 才会进入onScale()这个函数  否则多指触控一直走onScaleBegin方法 不走 onScale和 onScaleEnd方法
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        //        Log.e("ScaleGestrueDetector", "onScaleEnd");
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        //双击放大与缩小事件传递给GestureDetector 放在最前面 防止双击时还能产生移动的事件响应
        if (mGestureDetector.onTouchEvent(event)) {
            return true
        }

        //将手势传递给ScaleGestureDetector
        val onTouchEvent = mScaleGestureDetector.onTouchEvent(event)

        //-------------------------将放大的图片自由移动逻辑处理-----------------start------------
        //得到触控中心点的坐标
        var pointerX = 0f
        var pointerY = 0f
        //拿到多点触控的数量
        val pointerCount = event.pointerCount
        //        Log.i("pointerCount", "pointerCount:"+pointerCount);
        for (i in 0 until pointerCount) {
            pointerX += event.getX(i)
            pointerY += event.getY(i)
        }
        pointerX /= pointerCount.toFloat()
        pointerY /= pointerCount.toFloat()
        if (mLastPointerCount != pointerCount) {
            //手指发生改变时 需要重新判断 是否能够移动
            isCanDrag = false
            mLastPointerX = pointerX
            mLastPointerY = pointerY
        }
        mLastPointerCount = pointerCount
        val rectF = matrixRectF
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //赋值手指按下时间
                mFirstClickTime = System.currentTimeMillis()
                //赋手指按下坐标
                mLongPoint.x = event.x.toInt()
                mLongPoint.y = event.y.toInt()
                if (parent is ViewPager) {
                    //如果图片放大时 处理图片平移与ViewPager的滑动冲突
                    if (rectF.width() - width > 0.01 || rectF.height() - height > 0.01) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //如果图片放大时 处理图片平移与ViewPager的滑动冲突
                if (parent is ViewPager) {
                    if (rectF.width() - width > 0.01 || rectF.height() - height > 0.01) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                var dx = pointerX - mLastPointerX
                var dy = pointerY - mLastPointerY

//                //如果向右移动图片到了尽头，那么就不要拦截事件，让viewPager处理
//                if (rectF.left >= 0 && dx > 0) {
//                    parent.requestDisallowInterceptTouchEvent(false)
//                }
//                //如果向左移动到了尽头，那么就不要拦截事件，让viewPager处理
//                if (rectF.right <= width && dx < 0) {
//                    parent.requestDisallowInterceptTouchEvent(false)
//                }

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy)
                }
                if (isCanDrag) {
                    if (drawable != null) {
                        isCheckTopAndBottom = true
                        isCheckLeftAndRight = isCheckTopAndBottom
                        //如果图片宽度小于控件宽度 不允许横向移动
                        if (rectF.width() < width) {
                            isCheckLeftAndRight = false
                            dx = 0f
                        }
                        //如果图片的高度小于控件的高度 不允许纵向移动
                        if (rectF.height() < height) {
                            isCheckTopAndBottom = false
                            dy = 0f
                        }

                        mMatrix.postTranslate(dx, dy)
                        checkBorderWhenTranslate()
                        imageMatrix = mMatrix
                    }
                }
                mLastPointerX = pointerX
                mLastPointerY = pointerY
                //手指微移一点也可以触发长按点击事件
                isScaling = !((event.x - mLongPoint.x in -5..5) && (event.y - mLongPoint.y in -5..5))
            }
            MotionEvent.ACTION_UP -> {
                //赋值手指松开时间
                mSecondClickTime = System.currentTimeMillis()
                //松开时再次变为false
                isScaling = false
                mLastPointerCount = 0
            }
//            MotionEvent.ACTION_CANCEL -> {
//                mLastPointerCount = 0
//            }
        }
        //-------------------------将放大的图片自由移动逻辑处理-------------------end----------
        return false
    }

    /**
     * 当移动时 进行边界检查
     */
    private fun checkBorderWhenTranslate() {

        val rect = matrixRectF
        var deltaX = 0f
        var deltaY = 0f

        val width = width
        val height = height

        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top
        }
        if (rect.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rect.bottom
        }

        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left
        }
        if (rect.right < width && isCheckLeftAndRight) {
            deltaX = width - rect.right
        }

        mMatrix.postTranslate(deltaX, deltaY)
    }

    /**
     * 判断滑动的距离是否触发滑动的临界条件
     *
     * @param dx
     * @param dy
     * @return
     */
    private fun isMoveAction(dx: Float, dy: Float): Boolean {
        return Math.sqrt((dx * dx + dy * dy).toDouble()) > mTouchSlop
    }
}