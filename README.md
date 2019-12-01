# android-http-module
안드로이드 Kotlin HTTP 프로토콜을 쉽게 처리하기 위한 처리 모듈 


## Getting Started 
minSdkVersion 21
targetSdkVersion 29
kotlin 버전 1.3.61 

Android studio import 처리

## Dependencies 
Gson 2.8.6 

## 설명
Kotlin 기반의 Http 처리 모듈 

GET, POST, PUT, DELETE 로 HTTP 처리 모듈 


GET (With AsyncTask) 
~~~kotlin
  val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/getData", HttpUtil.Method.GET)
  request.putForm("data1", "test")  // 파라메터
  request.putForm("data2", "1")     // 파라메터 
  request.putHeader("header1", "header1") // 헤더값

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
~~~ 

POST, PUT, DELETE (With AsyncTask, Request Body 로 입력) 
~~~kotlin
      // HttpUtil.Method 는 GET, POST, PUT, DELETE, MULTIPART(파일 업로드) 존재 
      val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/postData", HttpUtil.Method.POST) 
      val statusModel = Status(ax=0, ay=1, az=2,
            gx=2, gy=4, gz=5,
            heartBeat = 6, spO2 = 7) // 객체 형태
      request.putHeader("header1", "header1")
      request.putForm(statusModel) // 객체 형태를 Request JSON Body 형태로 입력 

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
~~~

MULTIPART (With AsyncTask) 
~~~kotlin
      val request = RequestModel("https://www.hyper-link.kr:8445/server/api/test/postData", HttpUtil.Method.MULTIPART)
      request.putHeader("header1", "header1")
      request.putForm("name", "value")        //key:value 의 멀티파트 파라메터 
      request.putFile("fileName", File()) // 예제를 위해 생성자 호출하나 특정 경로의 이미지나 동영상을 가져와 해당 객체로 입력

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

~~~
