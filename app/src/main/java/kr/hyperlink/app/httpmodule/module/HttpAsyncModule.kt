package kr.hyperlink.app.httpmodule.module

import android.os.AsyncTask
import kr.hyperlink.app.httpmodule.module.HttpUtil.Method.*
import java.net.HttpURLConnection

/**
 * Created by rxgp1@hyper-link.com on 2019-12-01
 * Company : Hyper link
 * Author : Alex
 */
class HttpAsyncModule  : AsyncTask<RequestModel, Void, ResponseModel>() {

    interface OnResponse {
        // 결과 처리
        fun onSuccessResponse (result: ResponseModel, RequestCode: Int?)

        // 결과 실패 일경우
        fun onFailedResponse (status: Int, RequestCode: Int?)
    }

    private lateinit var requestModel: RequestModel

    private var callback: OnResponse? = null


    fun setOnResponse(callback: OnResponse) {
        this.callback = callback
    }

    override fun doInBackground(vararg request: RequestModel?): ResponseModel {
        request.first()?.let{
            requestModel = it
        }
        var responseModel = ResponseModel()
        if (requestModel == null) {
            responseModel.status = HttpURLConnection.HTTP_BAD_REQUEST
            return responseModel
        }

        val http = HttpUtil(requestModel)
        return when (requestModel.getMethod()) {
            GET -> {
                http.get()
            }
            POST -> {
                http.post()
            }
            POST_FORM -> {
                http.postForm()
            }
            PUT -> {
                http.put()
            }
            DELETE -> {
                http.delete()
            }
            MULTIPART -> {
                http.multipart()
            }
        }
    }

    override fun onPostExecute(response: ResponseModel) {
        super.onPostExecute(response)
        val status = response.status
        if (status == HttpURLConnection.HTTP_OK) {
            callback?.onSuccessResponse(response, requestModel.getRequestCode())
        } else {
            callback?.onFailedResponse(status, requestModel.getRequestCode())
        }
    }

    fun execute (request: RequestModel) {
        if(this.status == Status.RUNNING) {
            super.cancel(true)
        }
        super.execute(request)
    }
}