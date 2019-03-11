//package com.mineyang.yang.utils
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.media.MediaMetadataRetriever
//import android.media.ThumbnailUtils
//import android.os.Looper
//import android.provider.MediaStore
//import android.text.TextUtils
//import android.widget.ImageView
//import mineyang.com.yang.R
//import java.io.File
//import java.math.BigDecimal
//import java.security.MessageDigest
//import java.util.*
//
//
///**Glide缓存工具类
// * Created by Trojx on 2016/10/10 0010.
// */
//object GlideCacheUtils {
//    private var inst: GlideCacheUtils? = null
//
//    fun getInstance(): GlideCacheUtils {
//        if (inst == null) {
//            inst = GlideCacheUtils
//        }
//        return inst as GlideCacheUtils
//    }
//
//    /**
//     * 清除图片磁盘缓存
//     */
//    fun clearImageDiskCache(context: Context) {
//        try {
//            if (Looper.myLooper() === Looper.getMainLooper()) {
//                Thread(Runnable {
//                    Glide.get(context).clearDiskCache()
//                    //                        BusUtil.getBus().post(new GlideCacheClearSuccessEvent());
//                }).start()
//            } else {
//                Glide.get(context).clearDiskCache()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * 清除图片内存缓存
//     */
//    fun clearImageMemoryCache(context: Context) {
//        try {
//            if (Looper.myLooper() === Looper.getMainLooper()) { //只能在主线程执行
//                Glide.get(context).clearMemory()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//
//    /**
//     * 清除图片所有缓存
//     */
//    fun clearImageAllCache(context: Context) {
//        clearImageDiskCache(context)
//        clearImageMemoryCache(context)
//        val ImageExternalCatchDir = context.externalCacheDir.path + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
//        deleteFolderFile(ImageExternalCatchDir, true)
//    }
//
//    /**
//     * 获取Glide造成的缓存大小
//     *
//     * @return CacheSize
//     */
//    fun getCacheSize(context: Context): String {
//        try {
//            return getFormatSize(getFolderSize(File(context.cacheDir.path + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)).toDouble())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return ""
//    }
//
//    /**
//     * 获取指定文件夹内所有文件大小的和
//     *
//     * @param file file
//     * @return size
//     * @throws Exception
//     */
//    @Throws(Exception::class)
//    private fun getFolderSize(file: File): Long {
//        var size: Long = 0
//        try {
//            val fileList = file.listFiles()
//            for (aFileList in fileList) {
//                if (aFileList.isDirectory) {
//                    size += getFolderSize(aFileList)
//                } else {
//                    size += aFileList.length()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return size
//    }
//
//    /**
//     * 删除指定目录下的文件，这里用于缓存的删除
//     *
//     * @param filePath       filePath
//     * @param deleteThisPath deleteThisPath
//     */
//    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
//        if (!TextUtils.isEmpty(filePath)) {
//            try {
//                val file = File(filePath)
//                if (file.isDirectory) {
//                    val files = file.listFiles()
//                    for (file1 in files) {
//                        deleteFolderFile(file1.absolutePath, true)
//                    }
//                }
//                if (deleteThisPath) {
//                    if (!file.isDirectory) {
//                        file.delete()
//                    } else {
//                        if (file.listFiles().isEmpty()) {
//                            file.delete()
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    /**
//     * 格式化单位
//     *
//     * @param size size
//     * @return size
//     */
//    private fun getFormatSize(size: Double): String {
//
//        val kiloByte = size / 1024
////        if (kiloByte < 1) {
////            return size.toString() + "Byte"
////        }
//
//        val megaByte = kiloByte / 1024
//        if (megaByte < 1) {
//            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
//            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
//        }
//
//        val gigaByte = megaByte / 1024
//        if (gigaByte < 1) {
//            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
//            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
//        }
//
//        val teraBytes = gigaByte / 1024
//        if (teraBytes < 1) {
//            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
//            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
//        }
//        val result4 = BigDecimal(teraBytes)
//
//        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
//    }
//
//    /**
//     *  获取视频某一帧加载为缩略图
//     *  @param context 上下文
//     *  @param uri 视频地址
//     *  @param imageView 设置image
//     *  @param frameTimeMicros 获取某一时间帧
//     */
//    @SuppressLint("CheckResult")
//    fun loadVideoScreenshot(context: Context, uri: String, imageView: ImageView, frameTimeMicros: Long) {
//        val requestOptions = RequestOptions.frameOf(frameTimeMicros)
//        requestOptions.set(VideoBitmapDecoder.FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST)
//        requestOptions.transform(object : BitmapTransformation() {
//            override fun updateDiskCacheKey(messageDigest: MessageDigest) {
//                try {
//                    messageDigest.update((context.packageName + "RotateTransform").toByteArray())
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
//                return toTransform
//            }
//        })
//        Glide.with(context).load(uri).apply(requestOptions).into(imageView)
//    }
//
//    //获取视频第一帧
//    fun createVideoThumbnail(context: Context, filePath: String, kind: Int): Bitmap {
//        var bitmap: Bitmap? = null
//        val retriever = MediaMetadataRetriever()
//        try {
//            if (filePath.startsWith("http://")
//                    || filePath.startsWith("https://")
//                    || filePath.startsWith("widevine://")) {
//                retriever.setDataSource(filePath, Hashtable<String, String>())
//            } else {
//                retriever.setDataSource(filePath)
//            }
//            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //retriever.getFrameAtTime(-1);
//        } catch (ex: IllegalArgumentException) {
//            // Assume this is a corrupt video file
//            ex.printStackTrace()
//        } catch (ex: RuntimeException) {
//            // Assume this is a corrupt video file.
//            ex.printStackTrace()
//        } finally {
//            try {
//                retriever.release()
//            } catch (ex: RuntimeException) {
//                // Ignore failures while cleaning up.
//                ex.printStackTrace()
//            }
//        }
//
//        if (bitmap == null) {
//            return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img_icon)
//        }
//
//        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {//压缩图片 开始处
//            // Scale down the bitmap if it's too large.
//            val width = bitmap.width
//            val height = bitmap.height
//            val max = Math.max(width, height)
//            if (max > 512) {
//                val scale = 512f / max
//                val w = Math.round(scale * width)
//                val h = Math.round(scale * height)
//                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
//            }//压缩图片 结束处
//        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
//                    96,
//                    96,
//                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//        }
//        return bitmap!!
//    }
//}