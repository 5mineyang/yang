package com.mineyang.yang.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.mineyang.yang.R
import com.mineyang.yang.utils.ToastUtil

/**
 *  Description : 邮箱输入提示

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2018/8/14
 */
class EmailAutoCompleteTextView : AutoCompleteTextView {
    private var emailSufixs = arrayOf("@qq.com", "@163.com", "@126.com", "@gmail.com", "@sina.com", "@hotmail.com", "@yahoo.cn", "@sohu.com", "@foxmail.com", "@139.com", "@yeah.net", "@vip.qq.com", "@vip.sina.com")

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    fun setAdapterString(es: Array<String>?) {
        if (es != null && es.isNotEmpty())
            this.emailSufixs = es
    }

    private fun init(context: Context) {
        //adapter中使用默认的emailSufixs中的数据，可以通过setAdapterString来更改
        this.setAdapter(EmailAutoCompleteAdapter(context, R.layout.layout_autocompletetextview_item, emailSufixs))
        //使得在输入1个字符之后便开启自动完成
        this.threshold = 1
        this.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text = this@EmailAutoCompleteTextView.text.toString()
                //当该文本域重新获得焦点后，重启自动完成
                if ("" != text)
                    performFiltering(text, 0)
            } else {
                //当文本域丢失焦点后，检查输入email地址的格式
                val ev = v as EmailAutoCompleteTextView
                val text = ev.text.toString()
                //正则
                if (text != null && text.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*".toRegex())) {

                } else {
                    ToastUtil.showShort("邮箱格式输入不正确")
                }
            }
        }
    }

    override fun replaceText(text: CharSequence) {
        //当我们在下拉框中选择一项时，android会默认使用AutoCompleteTextView中Adapter里的文本来填充文本域
        //因为这里Adapter中只是存了常用email的后缀
        //因此要重新replace逻辑，将用户输入的部分与后缀合并
        var t = this.text.toString()
        val index = t.indexOf("@")
        if (index != -1)
            t = t.substring(0, index)
        super.replaceText(t + text)
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        //该方法会在用户输入文本之后调用，将已输入的文本与adapter中的数据对比，若它匹配
        //adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
        val t = text.toString()
        //因为用户输入邮箱时，都是以字母，数字开始，而我们的adapter中只会提供以类似于"@163.com"
        //的邮箱后缀，因此在调用super.performFiltering时，传入的一定是以"@"开头的字符串
        val index = t.indexOf("@")
        if (index == -1) {
            if (t.matches("^[a-zA-Z0-9_]+$".toRegex())) {
                super.performFiltering("@", keyCode)
            } else
                this.dismissDropDown()//当用户中途输入非法字符时，关闭下拉提示框
        } else {
            super.performFiltering(t.substring(index), keyCode)
        }
    }

    private inner class EmailAutoCompleteAdapter(context: Context, textViewResourceId: Int, email_s: Array<String>) : ArrayAdapter<String>(context, textViewResourceId, email_s) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v: View? = convertView
            if (v == null)
                v = LayoutInflater.from(context).inflate(
                        R.layout.layout_autocompletetextview_item, null)
            val tv = v!!.findViewById<TextView>(R.id.tv)
            var t = this@EmailAutoCompleteTextView.text.toString()
            val index = t.indexOf("@")
            if (index != -1)
                t = t.substring(0, index)
            //将用户输入的文本与adapter中的email后缀拼接后，在下拉框中显示
            tv.text = t + getItem(position)
            return v
        }
    }
}