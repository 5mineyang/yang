package com.mineyang.yang.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileNotFoundException

/**
 * 本地文件相关
 */
object SDPathUtils {
    private val TAG = "UpdateFile"

    /* Checks if external storage is available for read and write */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 获取SD卡的可用空间大小（单位 byte）
     */
    fun getSDCardSpace(): Long {
        var sdCardSize: Long = 0
        if (isExternalStorageWritable()) {
            val sdcardDir = Environment.getExternalStorageDirectory()
            val sf = StatFs(sdcardDir.path)
            sdCardSize = sf.availableBytes
        }
        return sdCardSize
    }

    /**
     * 获取SD卡剩余空间大小 （单位 byte）
     */
    private fun getDataDirSpace(): Long {
        val root = Environment.getDataDirectory()
        val sf = StatFs(root.path)
        return sf.availableBytes
    }

    // 获取SD卡的根目录 /storage/sdcard0
    fun getSDCardBaseDir(type: String? = null): String? =
            if (isExternalStorageWritable()) {
                if (type != null) Environment.getExternalStoragePublicDirectory(type).toString() else Environment.getExternalStorageDirectory().absolutePath
            } else null

    // 获取内部存储根目录 /data
    fun getDataBaseDir(): String = Environment.getDataDirectory().absolutePath

    /**
     * 获取SD卡公有目录的文件,SD卡卸载就取系统目录
     */
    fun getPublicStorageFile(filename: String, type: String? = null, create: Boolean = true): File? =
            createFile(getPublicStoragePathSub(filename, type), create)

    /**
     * 获取SD卡公有目录的目录文件,SD卡卸载就取系统目录
     */
    fun getPublicStorageDir(child: String, type: String? = null): String {
        val filePath = getPublicStoragePathSub(child, type)
        val file = File(filePath)
        file.mkdirs()
        return filePath
    }

    /**
     * 获取SD卡公有目录的文件路径,SD卡卸载就取系统目录
     */
    fun getPublicStoragePath(filename: String, type: String? = null): String {
        val filePath = getPublicStoragePathSub(filename, type)
        File(filePath).parentFile.mkdirs()
        return filePath
    }

    /**
     * 获取SD卡公有目录的文件,SD卡卸载就取系统目录
     */
    fun getSDCardPrivateCacheFile(context: Context, child: String? = null, create: Boolean = true): File? =
            createFile(getPrivateCachePathSub(context, child), create)


    /**
     * 获取缓存目录的文件目录，卸载APP 文件会删除
     */
    fun getPrivateCacheDir(context: Context, child: String? = null): String {
        val filePath: String = getPrivateCachePathSub(context, child)
        val file = File(filePath)
        file.mkdirs()
        return filePath
    }

    /**
     * 清空文件夹
     */
    fun clearFolder(path: String) {
        clearFolder(File(path))
    }

    fun clearFolder(file: File) {
        if (file.isFile) {
            file.delete()
            return
        }
        if (file.isDirectory) {
            val childFile = file.listFiles()
            if (childFile == null || childFile.isEmpty()) {
                file.delete()
                return
            }
            for (f in childFile) {
                clearFolder(f)
            }
            file.delete()
        }
    }

    /**
     * 获取目录长度
     */
    fun getFolderLength(path: String): Long {
        return getFolderLength(File(path))
    }

    fun getFolderLength(file: File?): Long {
        var size: Long = 0
        try {
            if (file != null) {
                val fileList = file.listFiles()
                if (fileList != null && fileList.isNotEmpty()) {
                    for (i in fileList.indices) {
                        if (fileList[i].isDirectory) {
                            size += getFolderLength(fileList[i])
                        } else {
                            size += fileList[i].length()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }
    /**
     * 保存文件通知系统更新，在图库显示图片
     */
    fun updateImageSysStatu(context: Context?, path: String, applicationId: String) {
        if (!TextUtils.isEmpty(path)) {
            val file = File(path)
            if (context != null && file.exists()) {
                // 把文件插入到系统图库
                try {
                    //insertImage 可能会有2个，系统也生成一个
//                    MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, "image", "图片")
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                updateFileStatu(context, file, applicationId)
            }

        } else {
            Log.d(TAG, "updateFileStatu: path is null")
        }
    }

    /**
     * 保存文件通知系统更新，在图库显示图片
     */
    fun updateFileStatu(context: Context?, file: File?, applicationId: String) {
        if (context != null && file != null && file.exists()) {
            //通知图库更新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val uri: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(context, "${applicationId}.fileprovider", file)
                } else {
                    uri = Uri.fromFile(file)
                }
                intent.data = uri
                context.sendBroadcast(intent)
            } else {
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())))
            }
        } else {
            Log.d(TAG, "updateFileStatu: file is not exist")
        }
    }

    /**
     * 获取SD卡公有目录的路径,SD卡卸载就取系统目录
     */
    private fun getPublicStoragePathSub(filename: String, type: String? = null): String {
        val baseDir = getSDCardBaseDir(type) ?: getDataBaseDir()
        val filePath = """$baseDir${File.separator}$filename"""
        return filePath
    }

    /**
     * 获取私有缓存目录,SD卡卸载就取系统目录
     */
    private fun getPrivateCachePathSub(context: Context, childPath: String? = null): String {
        val baseDir: String
        if (isExternalStorageWritable()) {
            if (context.externalCacheDir != null) {
                baseDir = context.externalCacheDir.absolutePath//  /mnt/sdcard/Android/data/com.my.app/cache
            } else {
                baseDir = Environment.getExternalStorageDirectory().path//  /mnt/sdcard
            }
        } else {
            if (context.cacheDir != null) {
                baseDir = context.cacheDir.absolutePath//  /data/data/com.my.app/cache
            } else {
                baseDir = Environment.getDataDirectory().absolutePath//  /data
            }
        }
        val filePath = """$baseDir${File.separator}$childPath"""
        return filePath
    }

    /**
     * 获取文件
     * @param filePath 文件路径
     * @param create 不存在是否创建目录和文件，默认创建
     */
    private fun createFile(filePath: String, create: Boolean): File? {
        val file = File(filePath)
        return if (file.exists()) {
            file
        } else {
            if (create) {
                file.parentFile.mkdirs()
                file.createNewFile()
                file
            } else
                null
        }
    }
}