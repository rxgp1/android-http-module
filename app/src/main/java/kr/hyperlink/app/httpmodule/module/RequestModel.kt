package kr.hyperlink.app.httpmodule.module

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Created by rxgp1@hyper-link.com on 2019-12-01
 * Company : Hyper link
 * Author : Alex
 */

class RequestModel {

    // 폼처리
    private val forms: MutableMap<String, Any> = mutableMapOf()

    // 파일 전송 객체
    private val files: MutableMap<String, File> = mutableMapOf()

    // 전송 헤더
    private val headers: MutableMap<String, String> = mutableMapOf()

    // 전송 URL
    private var url: String

    private var method: HttpUtil.Method

    private var requestCode: Int? = null

    constructor(url: String) {
        this.url = url
        this.method = HttpUtil.Method.GET
    }

    constructor(url: String, requestCode: Int?) {
        this.url = url
        this.method = HttpUtil.Method.GET
        this.requestCode = requestCode
    }

    constructor(url: String, method: HttpUtil.Method, requestCode: Int?) {
        this.url = url
        this.method = method
        this.requestCode = requestCode
    }

    fun putHeader(key: String, value: String) {
        this.headers[key] =  value
    }

    fun getHeaders(): MutableMap<String, String> {
        return this.headers
    }

    fun putForm(key: String, value: Any) {
        this.forms[key] =  value
    }

    fun putForm(body: Any) {
        try {
            with(Gson()) {
                val json = this.toJson(body)
                var statusMap: MutableMap<String, Any> = this.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
                forms.putAll(statusMap)
            }
        } catch (e: Exception) { }
    }

    fun getForms(): MutableMap<String, Any>  {
        return this.forms
    }

    fun putFile(key: String, file: File) {
        this.files[key] =  file
    }

    fun getFiles(): MutableMap<String, File> {
        return this.files
    }

    fun getURL(): String {
        return this.url
    }

    fun getMethod(): HttpUtil.Method {
        return this.method
    }

    fun getRequestCode(): Int? {
        return this.requestCode
    }
}

