package com.mineyang.yang.base

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by zq on 2018/9/6
 */
class SealAccountInterceptor : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response = chain.proceed(request)
        val responseBody = response.body()
        responseBody?.let {
            val contentLength = it.contentLength()
            if (!bodyEncoded(response.headers())) {
                val source = it.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()

                var charset: Charset? = UTF8
                val contentType = it.contentType()
                contentType?.let {
                    try {
                        charset = it.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        return response
                    }
                }
                if (!isPlaintext(buffer)) {
                    return response
                }
//                if (contentLength != 0L) {
//                    val result = buffer.clone().readString(charset)
//                    try {
//                        val json = JSONObject(result)
//                        if (json.optInt("code") == 23000) {//重复登录
//                            loginOut()
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        return response
//                    }
//                }
            }
        }

        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    @Throws(EOFException::class)
    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }
}