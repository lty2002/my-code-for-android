@file:OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)

package com.lty923.mycode

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.ctrip.flight.mmkv.defaultMMKV
import com.ctrip.flight.mmkv.initialize
import com.lty923.mycode.ui.theme.MyCodeTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Thread.sleep


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val rootDir = initialize(applicationContext)
        val kv = defaultMMKV()
        Log.d("MMKV Path", rootDir)
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (kv.containsKey("userid") && kv.containsKey("card")) {
            // 加载二维码
            val userId = kv.takeString("userid")
            val card = kv.takeString("card")
            Log.d("storage", userId)
            Log.d("storage", card)

            // 实例化request
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://wxmicro.rayroccloud.com/WXAPI/GetQr?userid=$userId&card=$card&Tenid=1248985250630078464&PojNo=9")
                .get()
                .build()

            GlobalScope.launch {
                while (true) {
                    delay(1000)
                    // 使用子线程进行通信
                    GlobalScope.launch(Dispatchers.IO) {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val codeContent = response.body!!.bytes().toString(Charsets.UTF_8)
                            GlobalScope.launch(Dispatchers.Main) {
                                setContent {
                                    MyCodeTheme {
                                        Surface(
                                            Modifier.systemBarsPadding(),
                                            color = MaterialTheme.colorScheme.background
                                        ) {
                                            MainCode(content = "$codeContent")
                                        }
                                    }
                                }
                            }
                        } else {
                            GlobalScope.launch(Dispatchers.Main) {
                                setContent {
                                    MyCodeTheme {
                                        Surface(
                                            Modifier.systemBarsPadding(),
                                            color = MaterialTheme.colorScheme.background
                                        ) {
                                            MainCode(content = "error")
                                        }
                                    }
                                }
                            }
                        }
                        sleep(1000)
                    }
                }
            }
        } else {
            // 初始化界面
            setContent {
                MyCodeTheme {
                    Surface(
                        Modifier.systemBarsPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        StartView()
                    }
                }
            }
        }
    }
}

@Composable
fun StartView(modifier: Modifier = Modifier) {
    val kv = defaultMMKV()
    Column(
        Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var infoString by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }
        OutlinedTextField(
            value = infoString,
            onValueChange = { infoString = it },
            label = { Text("用户信息") }
        )
        Button(onClick = {
            val s = infoString.text
            val userid = s.split('-')[0]
            val card = s.split('-')[1]
            kv.set("userid", userid)
            kv.set("card", card)
        }) {
            Text(text = "保存（需重启应用）")
        }
    }
}

@Composable
fun MainCode(content: String, modifier: Modifier = Modifier) {
    val kv = defaultMMKV()
    Column(
        Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val codeImage = Utility.generateQR(content)
        if (codeImage != null) {
//            Text(content)
            Image(
                bitmap = codeImage,
                contentDescription = "Generated QRCode"
            )
        } else {
            Text(content)
            Text("Error")
        }
        Button(onClick = {
            kv.removeValuesForKeys(listOf("userid", "key"))
        }) {
            Text(text = "清除信息（需重启应用）")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodePreview() {
    MyCodeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
//            MainCode(content = "1")
        }
    }
}