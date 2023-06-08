package com.hellobike.magiccube.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

class FileDownloader {


    interface IDownloadListener {
        fun onSuccess(url: String, json: String)
        fun onError(url: String, code: Int, message: String)
    }

    private val tasks: ConcurrentHashMap<String, ArrayList<DownloadTask>> by lazy {
        ConcurrentHashMap<String, ArrayList<DownloadTask>>()
    }

    private val service by lazy { NetCore.createDownloadService() }
    private val coroutine: CoroutineSupport = CoroutineSupport()

    fun download(url: String, listener: IDownloadListener) {

        synchronized(this) {
            val task = DownloadTask()
            task.listener = listener

            if (tasks.containsKey(url)) {
                tasks[url]?.add(task)
                return
            }
            val taskList = ArrayList<DownloadTask>()
            taskList.add(task)
            tasks[url] = taskList
        }

        coroutine.launch(Dispatchers.IO) {
            val call = service.download(url)
            try {
                val response = call.execute()
                val responseBody = response.body()
                if (responseBody == null) {
                    synchronized(this) {
                        tasks.remove(url)?.forEach { it.listener?.onError(url, -1, "下载失败！") }
                    }
                    return@launch
                }
                val json = writeResponseBodyToDisk(responseBody)

                synchronized(this) {
                    tasks.remove(url)?.forEach { it.listener?.onSuccess(url, json) }
                }
            } catch (t: Throwable) {
                t.printStackTrace()

                synchronized(this) {
                    tasks.remove(url)
                        ?.forEach { it.listener?.onError(url, -2, t.message ?: t.toString()) }
                }
            }
        }
    }

    private fun writeResponseBodyToDisk(responseBody: ResponseBody): String {
        val input = responseBody.byteStream()
        val strBuffer = StringBuffer()
        val reader = BufferedReader(InputStreamReader(input))
        reader.useLines { it.forEach { line -> strBuffer.append(line) } }
        return strBuffer.toString()
    }


    class DownloadTask {
        var listener: IDownloadListener? = null
    }

}