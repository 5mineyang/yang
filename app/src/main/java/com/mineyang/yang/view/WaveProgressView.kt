package com.mineyang.yang.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.mineyang.yang.R
import java.text.DecimalFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：16/8/13-下午4:06
 * 描    述：下载动画progress
 * 修订历史：
 * ================================================
 */
class WaveProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var radius = dp2px(55)
    private val textColor: Int
    private val textSize: Int
    private val progressColor: Int
    private val radiusColor: Int
    private val textPaint: Paint
    private val circlePaint: Paint
    private val pathPaint: Paint
    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var minPadding: Int = 0
    private var progress = 0
    private val maxProgress: Float
    private val path = Path()
    private val df = DecimalFormat("0.0")

    init {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.WaveProgressView, defStyleAttr, R.style.WaveProgressViewDefault)
        radius = a.getDimension(R.styleable.WaveProgressView_radius, radius.toFloat()).toInt()
        textColor = a.getColor(R.styleable.WaveProgressView_progress_text_color, 0)
        textSize = a.getDimensionPixelSize(R.styleable.WaveProgressView_progress_text_size, 0)
        progressColor = a.getColor(R.styleable.WaveProgressView_progress_color, 0)
        radiusColor = a.getColor(R.styleable.WaveProgressView_radius_color, 0)
        progress = a.getInteger(R.styleable.WaveProgressView_progress, 0)
        maxProgress = a.getFloat(R.styleable.WaveProgressView_maxProgress, 100f)
        a.recycle()

        //初始化一些画笔
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = textSize.toFloat()
        textPaint.color = textColor
        textPaint.isDither = true


        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint.color = radiusColor
        circlePaint.isDither = true

        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.color = progressColor
        pathPaint.isDither = true
        pathPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //计算宽和高
        val exceptW = paddingLeft + paddingRight + 2 * radius
        val exceptH = paddingTop + paddingBottom + 2 * radius
        val width = View.resolveSize(exceptW, widthMeasureSpec)
        val height = View.resolveSize(exceptH, heightMeasureSpec)
        val min = Math.min(width, height)

        this.mHeight = min
        this.mWidth = this.mHeight

        //计算半径,减去padding的最小值
        val minLR = Math.min(paddingLeft, paddingRight)
        val minTB = Math.min(paddingTop, paddingBottom)
        minPadding = Math.min(minLR, minTB)
        radius = (min - minPadding * 2) / 2

        setMeasuredDimension(min, min)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, Bitmap.Config.ARGB_8888)
            bitmapCanvas = Canvas(bitmap!!)
        }
        bitmapCanvas!!.save()
        //移动坐标系
        bitmapCanvas!!.translate(minPadding.toFloat(), minPadding.toFloat())
        //绘制圆
        bitmapCanvas!!.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), circlePaint)

        //绘制PATH
        //重置绘制路线
        path.reset()
        val percent = progress * 1.0f / maxProgress
        val y = (1 - percent) * radius.toFloat() * 2f
        //移动到右上边
        path.moveTo((radius * 2).toFloat(), y)
        //移动到最右下方
        path.lineTo((radius * 2).toFloat(), (radius * 2).toFloat())
        //移动到最左下边
        path.lineTo(0f, (radius * 2).toFloat())
        //移动到左上边
        // path.lineTo(0, y);
        //实现左右波动,根据progress来平移
        path.lineTo(-(1 - percent) * radius.toFloat() * 2f, y)
        if (progress != 0) {
            //根据直径计算绘制贝赛尔曲线的次数
            val count = radius * 4 / 60
            //控制-控制点y的坐标
            val point = (1 - percent) * 15
            for (i in 0 until count) {
                path.rQuadTo(15f, -point, 30f, 0f)
                path.rQuadTo(15f, point, 30f, 0f)
            }
        }
        //闭合
        path.close()
        bitmapCanvas!!.drawPath(path, pathPaint)

        //绘制文字
        val text = "$progress%"
        val textW = textPaint.measureText(text)
        val fontMetrics = textPaint.fontMetrics
        val baseLine = radius - (fontMetrics.ascent + fontMetrics.descent) / 2
        bitmapCanvas!!.drawText(text, radius - textW / 2, baseLine, textPaint)

        bitmapCanvas!!.restore()

        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    fun getProgress(): Int {
        return progress
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }


    private class SavedState : View.BaseSavedState {
        internal var progress = 0

        constructor(source: Parcel) : super(source) {}

        constructor(superState: Parcelable) : super(superState) {}

        companion object {

            @SuppressLint("ParcelCreator")
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.progress = progress
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        setProgress(ss.progress)
    }
}
