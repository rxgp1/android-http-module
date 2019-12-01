package kr.hyperlink.app.httpmodule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.hyperlink.app.httpmodule.module.HttpAsyncModule
import kr.hyperlink.app.httpmodule.module.HttpUtil
import kr.hyperlink.app.httpmodule.module.RequestModel
import kr.hyperlink.app.httpmodule.module.ResponseModel

/**
 * Created by rxgp1@hyper-link.com on 2019-12-01
 * Company : Hyper link
 * Author : Alex
 */
class MainActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/getData", HttpUtil.Method.GET)
        request.putForm("data1", "test")
        request.putForm("data2", "1")
        request.putHeader("header1", "header1")

        val async = HttpAsyncModule()
        async.setOnResponse(object: HttpAsyncModule.OnResponse {
            override fun onSuccessResponse(result: ResponseModel, RequestCode: Int?) {
                // 결과 반환 (Http code 성공일시)
                println(result)
            }

            override fun onFailedResponse(status: Int, RequestCode: Int?) {
                // 결과 반환 (Http code 실패일시)
                println(status)
            }
        })
        async.execute(request)
    }
}