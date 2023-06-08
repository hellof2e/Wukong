package com.hellobike.magiccube.net

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

object NetCore {

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(Retrofit2ConverterFactory())
            .baseUrl("http://127.0.0.1")
            .build()
    }

    interface IDownloadService {

        @Streaming
        @GET
        fun download(@Url url: String): Call<ResponseBody>
    }

    interface ApiService {
        @POST
        fun postRequest(
            @Url url: String,
            @Body params: HashMap<String, Any?>
        ): Call<String>

        @GET
        fun getRequest(
            @Url url: String,
            @QueryMap params: HashMap<String, Any?>
        ): Call<String>
    }

    fun <T> createService(clz: Class<T>): T {
        return retrofit.create(clz)
    }

    fun createApiService(): ApiService {
        return createService(ApiService::class.java)
    }

    fun createDownloadService(): IDownloadService {
        return createService(IDownloadService::class.java)
    }
}