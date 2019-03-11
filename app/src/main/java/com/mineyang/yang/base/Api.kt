package com.mineyang.yang.base

import io.reactivex.Observable
import retrofit2.http.*

/**
 * 请求Api
 */
interface Api {
    @GET("")
    fun get(@Header("ACCESS-TOKEN") token: String): Observable<BaseBean<Any>>

    @POST("")
    @FormUrlEncoded
    fun post(@Field("password") password: String, @Header("ACCESS-TOKEN") token: String): Observable<BaseBean<Any>>

}