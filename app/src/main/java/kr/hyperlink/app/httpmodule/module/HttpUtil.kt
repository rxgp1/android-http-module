package kr.hyperlink.app.httpmodule.module

import com.google.gson.Gson
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * Created by rxgp1@hyper-link.com on 2019-12-01
 * Company : Hyper link
 * Author : Alex
 */
class HttpUtil {

    // Http 타입을 지정한다.
    enum class Method {
        GET,
        POST,
        POST_FORM,
        PUT,
        DELETE,
        MULTIPART
    }

    private val charset: String = "UTF-8"

    private val lineFeed: String = "\r\n"

    // 전송 모델
    private var requestModel: RequestModel

    //true일경우 https, false http;
    private var isHttpType: Boolean = false

    // Http code
    private var httpStatusCode: Int = HttpURLConnection.HTTP_INTERNAL_ERROR

    constructor(requestModel: RequestModel) {
        this.requestModel = requestModel
        this.isHttpType = requestModel.getURL().toLowerCase().startsWith("https")
    }

    fun get(): ResponseModel {
        val response = ResponseModel()
        var connection: HttpURLConnection? = null
        var url = requestModel.getURL()
        try {
            var params = requestModel.getForms()
            if (params.isNotEmpty()) {
                url = StringBuffer().append(url).append("?").append(encodeString(params)).toString()
            }
            connection = getConnection(Method.GET, requestModel.getHeaders(), url)
            this.httpStatusCode = connection.responseCode
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                response.status = this.httpStatusCode
                return response
            }
            response.json = getResponse(connection)
            response.status = this.httpStatusCode
        } catch (e: Exception) {
            e.printStackTrace()
            this.httpStatusCode = HttpURLConnection.HTTP_BAD_REQUEST
            response.status = this.httpStatusCode
        } finally {
            connection?.let{
                it.disconnect()
            }
        }
        return response
    }

    fun put(): ResponseModel {
        val response = ResponseModel()
        var connection: HttpURLConnection? = null
        var url = requestModel.getURL()
        try {
            var json: String? = null
            if(requestModel.getForms().isNotEmpty()) {
                with(Gson()) {
                    json = this.toJson(requestModel.getForms()).toString()
                }
            }
            connection = getConnection(Method.PUT, requestModel.getHeaders(), url, json)
            this.httpStatusCode = connection.responseCode
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                response.status = this.httpStatusCode
                return response
            }
            response.json = getResponse(connection)
            response.status = this.httpStatusCode

        } catch (e: Exception) {
            e.printStackTrace()
            this.httpStatusCode = HttpURLConnection.HTTP_BAD_REQUEST
            response.status = this.httpStatusCode
        } finally {
            connection?.let{
                it.disconnect()
            }
        }
        return response
    }

    fun delete(): ResponseModel {
        val response = ResponseModel()
        var connection: HttpURLConnection? = null
        var url = requestModel.getURL()
        try {
            var json: String? = null
            if(requestModel.getForms().isNotEmpty()) {
                with(Gson()) {
                    json = this.toJson(requestModel.getForms()).toString()
                }
            }
            connection = getConnection(Method.DELETE, requestModel.getHeaders(), url, json)
            this.httpStatusCode = connection.responseCode
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                response.status = this.httpStatusCode
                return response
            }
            response.json = getResponse(connection)
            response.status = this.httpStatusCode

        } catch (e: Exception) {
            e.printStackTrace()
            this.httpStatusCode = HttpURLConnection.HTTP_BAD_REQUEST
            response.status = this.httpStatusCode
        } finally {
            connection?.let{
                it.disconnect()
            }
        }
        return response
    }

    fun post(): ResponseModel {
        val response = ResponseModel()
        var connection: HttpURLConnection? = null
        var url = requestModel.getURL()
        try {
            var json: String? = null
            if(requestModel.getForms().isNotEmpty()) {
                with(Gson()) {
                    json = this.toJson(requestModel.getForms()).toString()
                }
            }
            connection = getConnection(Method.POST, requestModel.getHeaders(), url, json)
            this.httpStatusCode = connection.responseCode
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                response.status = this.httpStatusCode
                return response
            }
            response.json = getResponse(connection)
            response.status = this.httpStatusCode

        } catch (e: Exception) {
            e.printStackTrace()
            this.httpStatusCode = HttpURLConnection.HTTP_BAD_REQUEST
            response.status = this.httpStatusCode
        } finally {
            connection?.let{
                it.disconnect()
            }
        }
        return response
    }

    fun postForm(): ResponseModel {
        val response = ResponseModel()
        var connection: HttpURLConnection? = null
        var url = requestModel.getURL()
        try {
            var params = requestModel.getForms()
            if (params.isNotEmpty()) {
                url = StringBuffer().append(url).append("?").append(encodeString(params)).toString()
            }
            connection = getConnection(Method.POST, requestModel.getHeaders(), url)
            this.httpStatusCode = connection.responseCode
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                response.status = this.httpStatusCode
                return response
            }
            response.json = getResponse(connection)
            response.status = this.httpStatusCode

        } catch (e: Exception) {
            e.printStackTrace()
            this.httpStatusCode = HttpURLConnection.HTTP_BAD_REQUEST
            response.status = this.httpStatusCode
        } finally {
            connection?.let{
                it.disconnect()
            }
        }
        return response
    }

    fun multipart(): ResponseModel {
        val response = ResponseModel()
        var connection: HttpURLConnection? = null
        var url = requestModel.getURL()
        try {
            val boundary: String = "===" + System.currentTimeMillis() + "==="
            connection = getConnection(Method.MULTIPART, requestModel.getHeaders(), url)
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            var outputStream = connection.outputStream
            var outWriter =  OutputStreamWriter(outputStream, charset)
            var writer = PrintWriter(outWriter, true)
            for(form in requestModel.getForms()) {
                writer.append("--$boundary")
                writer.append(lineFeed)
                writer.append("Content-Disposition: form-data; name=\"" + form.key + "\"")
                writer.append(lineFeed)
                writer.append("Content-Type: text/plain; charset=$charset")
                writer.append(lineFeed)
                writer.append(lineFeed)
                if (form.value is String) {
                    writer.append(form.value as String)
                } else {
                    writer.append("")
                }
                writer.append(lineFeed)
                writer.flush()
            }
            for (multipartFile in requestModel.getFiles()) {
                multipartFile.value?.let {
                    var fileName = it.name
                    writer.append("--$boundary").append(lineFeed)
                    writer.append("Content-Disposition: form-data; name=\"" + multipartFile.key + "\"; filename=\"" + fileName + "\"").append(lineFeed)
                    writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(lineFeed)
                    writer.append("Content-Transfer-Encoding: binary").append(lineFeed)
                    writer.append(lineFeed)
                    writer.flush()

                    val inputStream = FileInputStream(it)
                    inputStream.copyTo(outputStream, 4096 * 4096)

                    outputStream.flush()
                    inputStream.close()

                    writer.append(lineFeed)
                    writer.flush()
                }
            }

            writer.append(lineFeed).flush()
            writer.append("--").append(boundary).append("--").append(lineFeed)
            writer.close()

            this.httpStatusCode = connection.responseCode
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                response.status = this.httpStatusCode
                return response
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            response.json = reader.use(BufferedReader::readText)
            response.status = this.httpStatusCode
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            this.httpStatusCode = HttpURLConnection.HTTP_BAD_REQUEST
            response.status = this.httpStatusCode
        } finally {
            connection?.disconnect()
        }
        return response
    }

    private fun getConnection(method: Method, headers: Map<String, String>?, url: String): HttpURLConnection {
        return this.getConnection(method, headers, url, null)
    }

    private fun getConnection (method: Method, headers: Map<String, String>?, url: String, json: String?): HttpURLConnection {
        var connection: HttpURLConnection? = null
        var context: SSLContext? = null
        var urlObj: URL? = null
        var os: OutputStream? = null
        try {
            urlObj = URL(url)

            connection = if (isHttpType) {
                urlObj.openConnection() as HttpsURLConnection
            } else {
                urlObj.openConnection() as HttpURLConnection
            }

            headers?.forEach { (key, value) ->
                connection.setRequestProperty(key, value)
            }

            connection.connectTimeout = 5000

            when (method) {
                Method.GET -> {
                    connection.requestMethod = "GET"
                    connection.useCaches = false
                }
                Method.POST -> {
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.doOutput = true
                }
                Method.PUT -> {
                    connection.requestMethod = "PUT"
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.doOutput = true
                }
                Method.DELETE -> {
                    connection.requestMethod = "DELETE"
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.doOutput = true
                }
                Method.MULTIPART -> {
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.doOutput = true
                    connection.doInput = true
                }
                Method.POST_FORM -> {
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    connection.doOutput = true
                    connection.doInput = true
                    connection.useCaches = false
                }
            }

            if (isHttpType) {
                // Set Hostname verification
                (connection as HttpsURLConnection).hostnameVerifier = HostnameVerifier { _, _ ->
                    // Ignore host name verification. It always returns true.
                    true
                }

                context = SSLContext.getInstance("TLS")
                context.init(null, arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {
                        // TODO Auto-generated method stub
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {
                        // TODO Auto-generated method stub
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        // TODO Auto-generated method stub
                        return null
                    }
                }), null)
                (connection).sslSocketFactory = context.socketFactory
            }

            if(method != Method.MULTIPART) {
                connection.connect()
                when (method) {
                    Method.POST, Method.PUT, Method.POST_FORM, Method.DELETE -> {
                        os = connection.outputStream
                        json?.let {
                            os.write(it.toByteArray(charset("UTF-8")))
                        }
                        os.flush()
                    }
                }
            }
            return connection
        } catch (e: Exception) {
            throw Exception(e)
        } finally {
            os?.close()
        }
    }

    private fun getResponse(connection: HttpURLConnection): String {
        try {
            return connection.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     *
     * @param params GET 방식의 파라메터
     * @return 파라메터를 GET parameter로 변환
     */
    private fun encodeString(params: MutableMap<String, Any>): String {
        var list = params.toList()

        var sb = StringBuffer(256)
        if(list.isNotEmpty()) {
            list.forEach { pair ->
                if (pair.second is String) {
                    sb.append(URLEncoder.encode(pair.first, "UTF-8") + "=" + URLEncoder.encode(pair.second as String, "UTF-8"))
                    val s = list.lastIndexOf(pair)
                    if (s != list.lastIndex) {
                        sb.append("&")
                    }
                }
            }
        }
        return sb.toString()
    }

}