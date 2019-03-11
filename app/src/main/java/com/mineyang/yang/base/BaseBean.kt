package com.mineyang.yang.base

import java.io.Serializable

/**
 * BaseBean
 */
class BaseBean<T> : Serializable {
    var code = 0
    var msg = ""
    var data: T? = null
}
