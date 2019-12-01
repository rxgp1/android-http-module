package kr.hyperlink.app.httpmodule

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.hyperlink.app.httpmodule.module.HttpAsyncModule
import kr.hyperlink.app.httpmodule.module.HttpUtil
import kr.hyperlink.app.httpmodule.module.RequestModel
import kr.hyperlink.app.httpmodule.module.ResponseModel
import org.junit.Test

import org.junit.Assert.*
import java.net.HttpURLConnection
import java.net.URLEncoder

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

data class Status  (var ax: Double,
                    var gx: Double,
                    var ay: Double,
                    var gy: Double,
                    var az: Double,
                    var gz: Double,
                    var heartBeat: Double,
                    var spO2: Double) {
}

class HttpUtiltest {

    @Test
    fun test_encodeString () {

        val sameEncodedstring: String = "key1=val1&key2=val2&key3=val3"

        val params: MutableMap<String, String> = mutableMapOf()

        params["key1"] = "val1"
        params["key2"] = "val2"
        params["key3"] = "val3"

        var list = params.toList()

        var sb = StringBuffer(256)
        if(list.isNotEmpty()) {
            list.forEach { pair ->
                sb.append(URLEncoder.encode(pair.first, "UTF-8") + "=" + URLEncoder.encode(pair.second, "UTF-8"))
                val s = list.lastIndexOf(pair)
                if(s != list.lastIndex) {
                    sb.append("&")
                }
            }
        }
        assertEquals(sameEncodedstring, sb.toString())
    }

    @Test
    fun test_httpGetWithParams () {
        val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/getData")
        request.putForm("data1", "test")
        request.putForm("data2", "1")
        request.putHeader("header1", "header1")

        val httpUtil = HttpUtil(request)
        val response = httpUtil.get()

        assertEquals(HttpURLConnection.HTTP_OK, response.status)

    }

    private fun convertHexToAscii(hexBytes: ByteArray?): List<Double> {
        hexBytes?.let {hex ->
            val builder = StringBuilder()
            hex.forEach {
                builder.append(it.toChar())
            }
            var str = builder.toString()
            str = str.replace("A", "")
            str = str.replace("B", "^")
            str = str.replace("C", "^")
            str = str.replace("D", "^")
            str = str.replace("E", "^")
            str = str.replace("F", "^")
            str = str.replace("G", "^")
            str = str.replace("H", "^")

            str = str.replace("|", "")
            str = str.replace("~", "")

            val spl = str.split("^")

            val arr: ArrayList<Double> = ArrayList()

            spl.forEach {
                arr.add(it.toDouble())
            }

            return arr
        }
        return ArrayList()
    }

    @Test
    fun test_jsonToObject () {
        val hexBytes = byteArrayOf(0x7C, 0x41, 0x2D, 0x31, 0x30, 0x30, 0x30, 0x30, 0x42, 0x2D, 0x35, 0x30, 0x30, 0x30, 0x43, 0x35, 0x30, 0x30, 0x30, 0x44, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x45, 0x2D, 0x35, 0x30, 0x30, 0x30, 0x46, 0x31, 0x30, 0x30, 0x30, 0x47, 0x31, 0x35, 0x30, 0x2E, 0x30, 0x35, 0x48, 0x39, 0x36, 0x7E)
        val arr = convertHexToAscii(hexBytes)

        val statusModel = Status(ax=arr[0], ay=arr[1], az=arr[2],
            gx=arr[3], gy=arr[4], gz=arr[5],
            heartBeat = arr[6], spO2 = arr[7])

        var gson = Gson()
        var json = gson.toJson(statusModel)

        var statusMap: MutableMap<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
        val jsonStatusMap: String = gson.toJson(statusMap)
        assertEquals(jsonStatusMap, json)
    }

    @Test
    fun test_httpPutWithBody () {

        val hexBytes = byteArrayOf(0x7C, 0x41, 0x2D, 0x31, 0x30, 0x30, 0x30, 0x30, 0x42, 0x2D, 0x35, 0x30, 0x30, 0x30, 0x43, 0x35, 0x30, 0x30, 0x30, 0x44, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x45, 0x2D, 0x35, 0x30, 0x30, 0x30, 0x46, 0x31, 0x30, 0x30, 0x30, 0x47, 0x31, 0x35, 0x30, 0x2E, 0x30, 0x35, 0x48, 0x39, 0x36, 0x7E)
        val arr = convertHexToAscii(hexBytes)

        val statusModel = Status(ax=arr[0], ay=arr[1], az=arr[2],
            gx=arr[3], gy=arr[4], gz=arr[5],
            heartBeat = arr[6], spO2 = arr[7])

        val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/putData")
        request.putForm(statusModel)
        request.putHeader("header1", "header1")

        val httpUtil = HttpUtil(request)
        val response = httpUtil.put()

        assertEquals(HttpURLConnection.HTTP_OK, response.status)

    }

    @Test
    fun test_httpPostFormWithBody () {

        val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/postFormData")
        request.putForm("data1", "data1")
        request.putForm("data2", "1")
        request.putHeader("header1", "header1")

        val httpUtil = HttpUtil(request)
        val response = httpUtil.postForm()

        assertEquals(HttpURLConnection.HTTP_OK, response.status)
    }

    @Test
    fun test_httpDeleteWithBody () {

        val hexBytes = byteArrayOf(0x7C, 0x41, 0x2D, 0x31, 0x30, 0x30, 0x30, 0x30, 0x42, 0x2D, 0x35, 0x30, 0x30, 0x30, 0x43, 0x35, 0x30, 0x30, 0x30, 0x44, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x45, 0x2D, 0x35, 0x30, 0x30, 0x30, 0x46, 0x31, 0x30, 0x30, 0x30, 0x47, 0x31, 0x35, 0x30, 0x2E, 0x30, 0x35, 0x48, 0x39, 0x36, 0x7E)
        val arr = convertHexToAscii(hexBytes)

        val statusModel = Status(ax=arr[0], ay=arr[1], az=arr[2],
            gx=arr[3], gy=arr[4], gz=arr[5],
            heartBeat = arr[6], spO2 = arr[7])

        val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/deleteData")
        request.putForm(statusModel)
        request.putHeader("header1", "header1")

        val httpUtil = HttpUtil(request)
        val response = httpUtil.delete()

        assertEquals(HttpURLConnection.HTTP_OK, response.status)
    }

    @Test
    fun test_httpAsyncGet() {
        val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/getData")
        request.putForm("data1", "test")
        request.putForm("data2", "1")
        request.putHeader("header1", "header1")

        val async = HttpAsyncModule()
        async.setOnResponse(object: HttpAsyncModule.OnResponse {
            override fun onSuccessResponse(result: ResponseModel, RequestCode: Int?) {
                println(result)
            }

            override fun onFailedResponse(status: Int, RequestCode: Int?) {
                println(status)
            }
        })
        async.execute(request)

    }

}
