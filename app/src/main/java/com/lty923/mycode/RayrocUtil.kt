package com.lty923.mycode

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request


object RayrocUtil {
    private val client = OkHttpClient()

//    @OptIn(DelicateCoroutinesApi::class)
//    fun renderCode(): {
//        val request = Request.Builder()
//            .url("https://wx.rayroccloud.com/WXAPI/GetQr?userid=1318933522953670656&card=121C465B23021000&Tenid=1248985250630078464&PojNo=9")
//            .get()
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            //Dispatchers.IO代表的是网络访问的子线程
//            val response = client.newCall(request).execute()
//            if (response.isSuccessful) {
//                val codeContent = response.body.toString()
//                GlobalScope.launch(Dispatchers.Main) {
//
//                }
//            }
//        }
//    }
}