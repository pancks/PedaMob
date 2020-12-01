package com.sidewinderz0ne.pedamob

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_control.*
import okhttp3.*
import okio.IOException


lateinit var id: String
var lat = 0f
var lon = 0f

class ControlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        initView()
        btMap.setOnClickListener {
            execAPI("http://103.140.90.58:8000/getmap",this)
        }
        btLock.setOnClickListener {
            execAPI("http://103.140.90.58:8000/lock",this)
        }
        btUnlock.setOnClickListener {
            execAPI("http://103.140.90.58:8000/unlock",this)
        }
        btStatus.setOnClickListener {
            execAPI("http://103.140.90.58:8000/status",this)
        }
    }

    private fun initView() {
        val prefMan = PrefManager(this)
        id = try {
            prefMan.id.toString()
        } catch (e: Exception) {
            Toasty.error(this, "$e").show()
            "0"
        }
        Log.d("controlmap", "id=$id")

        Glide.with(this)//GLIDE LOGO FOR LOADING LAYOUT
                .load(R.drawable.ic_launcher_background)
                .into(logo_loading)
        lottie.setAnimation("loading_circle.json")//ANIMATION WITH LOTTIE FOR LOADING LAYOUT
        @Suppress("DEPRECATION")
        lottie.loop(true)
        lottie.playAnimation()
    }

    fun execAPI(url: String, context: Context){
        progressBarHolder.visibility = View.VISIBLE
        val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idkey", id)
                .build()
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "null")
                .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            @Throws(IOException::class)
            override fun onFailure(call: Call, e: java.io.IOException) {
                runOnUiThread(Runnable { //Handle UI here
                    progressBarHolder.visibility = View.GONE
                    Toasty.error(context,"$e",Toasty.LENGTH_LONG).show()
                })
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                runOnUiThread(Runnable { //Handle UI here
                    progressBarHolder.visibility = View.GONE
                    Toasty.success(context,response.toString(),Toasty.LENGTH_LONG).show()
                    tvLoc.text = response.toString()
                })
            }
        })
    }
}