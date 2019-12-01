package kr.hyperlink.app.httpmodule.module

import java.net.HttpURLConnection

/**
 * Created by rxgp1@hyper-link.com on 2019-12-01
 * Company : Hyper link
 * Author : Alex
 */
class ResponseModel {

    var status: Int = HttpURLConnection.HTTP_OK

    var json: String? = null
}