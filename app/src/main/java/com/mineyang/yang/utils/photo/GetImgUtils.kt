package com.mineyang.yang.utils.photo

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.util.*

/**
 * Description : 获取相册工具
 */
object GetImgUtils {
    private val cameraPath = "/DCIM/Camera"
    private val screentShotsPath = "/DCIM/Screenshots"
    private val screentPicturePath = "/Pictures/Screenshots"

    /**
     * 获取截图路径
     */
    private val screenshotsPath: String
        get() {
            var path = Environment.getExternalStorageDirectory().toString() + screentShotsPath
            val file = File(path)
            if (!file.exists()) {
                path = Environment.getExternalStorageDirectory().toString() + screentPicturePath
            }
            return path
        }

    data class ImgBean(
            var mTime: Long,
            var imgUrl: String
    )

    /**
     * 获取相册中最新一张图片
     */
    fun getLatestPhoto(context: Context, limit: Int = 1): List<ImgBean> {
        //拍摄照片的地址
        val CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + cameraPath
        //截屏照片的地址
        val SCREENSHOTS_IMAGE_BUCKET_NAME = screenshotsPath
        //拍摄照片的地址ID
        val CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME)
        //截屏照片的地址ID
        val SCREENSHOTS_IMAGE_BUCKET_ID = getBucketId(SCREENSHOTS_IMAGE_BUCKET_NAME)
        //查询路径和修改时间
        val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED)
        //
        val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"
        //
        val selectionArgs = arrayOf(CAMERA_IMAGE_BUCKET_ID)
        val selectionArgsForScreenshots = arrayOf(SCREENSHOTS_IMAGE_BUCKET_ID)

        //检查camera文件夹，查询并排序
        val imgBeans = ArrayList<ImgBean>()
        var cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
        var index = 0
        while (cursor!!.moveToNext()) {
            if (index >= limit)
                break
            val mtime = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
            val imgUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            imgBeans.add(ImgBean(mtime, imgUrl))
            index++
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        //检查Screenshots文件夹
        //查询并排序
        cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgsForScreenshots,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")

        index = 0
        while (cursor!!.moveToNext()) {
            if (index >= limit)
                break
            val mtime = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
            val imgUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            imgBeans.add(ImgBean(mtime, imgUrl))
            index++
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        //排序对比时间
        Collections.sort(imgBeans) { imgBean, t1 -> (t1.mTime - imgBean.mTime).toInt() }
        val imgBeans1 = ArrayList<ImgBean>()
        index = 0
        for (imgBean in imgBeans) {
            if (index >= limit)
                break
            imgBeans1.add(imgBean)
            index++
        }
        return imgBeans1
    }

    private fun getBucketId(path: String): String {
        return path.toLowerCase().hashCode().toString()
    }

    //获取自适应宽高 比例太大 会改比例
    fun getImageSizeWrap(width: Int, height: Int, minWidth: Int, maxWidth: Int, minHeight: Int, maxHeight: Int): ImageSize {
        val imageSize = ImageSize()
        val ratioWH = width.toFloat() / height.toFloat()
        val ratioWHMax = maxWidth.toFloat() / minHeight.toFloat()
        val ratioWHMin = minWidth.toFloat() / maxHeight.toFloat()
        val ratioWrap = maxWidth.toFloat() / maxHeight.toFloat()
        var resultWidth = width
        var resultHeight = height
        if (ratioWH > ratioWHMax) {
            imageSize.isNoWrap = true
            resultWidth = maxWidth
            resultHeight = minHeight
        } else if (ratioWH < ratioWHMin) {

            resultWidth = minWidth
            resultHeight = maxHeight
        } else {
            if (ratioWH < ratioWrap) {
                if (height < minHeight) {
                    resultHeight = minHeight
                } else if (height > maxHeight) {
                    resultHeight = maxHeight
                } else {
                    resultHeight = height
                }
                resultWidth = (resultHeight * ratioWH).toInt()
            } else {
                if (width < minWidth) {
                    resultWidth = minWidth
                } else if (width > maxWidth) {
                    resultWidth = maxWidth
                } else {
                    resultWidth = width
                }
                resultHeight = (resultWidth / ratioWH).toInt()
            }
        }

        imageSize.width = resultWidth
        imageSize.height = resultHeight
        Log.i("", "getImageSizeWrap:  #width=$width #height=$height #resultWidth=$resultWidth #resultHeight=$resultHeight")
        return imageSize
    }

    class ImageSize {
        //是否太长或太高
        var isNoWrap: Boolean = false
        var width: Int = 0
        var height: Int = 0
    }
}
