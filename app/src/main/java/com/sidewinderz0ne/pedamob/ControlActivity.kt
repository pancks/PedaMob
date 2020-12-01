package com.sidewinderz0ne.pedamob

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_control.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


lateinit var id: String
var status = 500
var lat = 0f
var lon = 0f
var update = 0L
var date = ""

class ControlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        initView()
        btMap.setOnClickListener {
            execAPI("http://103.140.90.58:8000/getmap", this)
        }
        btLock.setOnClickListener {
            execAPI("http://103.140.90.58:8000/lock", this)
        }
        btUnlock.setOnClickListener {
            execAPI("http://103.140.90.58:8000/unlock", this)
        }
        btStatus.setOnClickListener {
            execAPI("http://103.140.90.58:8000/status", this)
        }
        btGMap.visibility = View.GONE
        btGMap.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lon")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent)
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
        Glide.with(this)//GLIDE LOGO FOR LOADING LAYOUT
                .load(R.drawable.ic_launcher_background)
                .into(logo_loading)
        lottie.setAnimation("loading_circle.json")//ANIMATION WITH LOTTIE FOR LOADING LAYOUT
        @Suppress("DEPRECATION")
        lottie.loop(true)
        lottie.playAnimation()
    }

    private fun execAPI(url: String, context: Context){
        progressBarHolder.visibility = View.VISIBLE
        val jsonObj = JSONObject()
        try {
            jsonObj.put("idkey", id)
        }catch (e: Exception){
            Toasty.error(context, "$e").show()
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObj.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onFailure(call: Call, e: java.io.IOException) {
                runOnUiThread {
                    progressBarHolder.visibility = View.GONE
                    Toasty.error(context, "$e", Toasty.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jObj = JSONObject(response.body.toString())
                status = try {
                    jObj.getInt("status")
                } catch (e: Exception) {
                    500
                }
                lat = try {
                    jObj.getString("lat").toFloat()
                } catch (e: Exception) {
                    0f
                }
                lon = try {
                    jObj.getString("lon").toFloat()
                } catch (e: Exception) {
                    0f
                }
                update = try {
                    jObj.getString("lon").toLong()
                } catch (e: Exception) {
                    0L
                }
                runOnUiThread {
                    if (url.contains("getmap") && lat != 0f) {
                        btGMap.visibility = View.VISIBLE
                    }
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    date = sdf.format(Date(update))
                    tvLoc.text = "status=$status, lat=$lat, lon=$lon, update=$update, date=$date"
                    progressBarHolder.visibility = View.GONE
                    Toasty.success(context, response.toString(), Toasty.LENGTH_LONG).show()
                    tvResponse.text = response.toString()
                }
            }
        })
    }
}