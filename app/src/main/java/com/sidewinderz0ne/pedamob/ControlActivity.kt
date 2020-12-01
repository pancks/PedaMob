package com.sidewinderz0ne.pedamob

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_control.*
import okhttp3.*
import okio.IOException
import org.json.JSONException
import org.json.JSONObject


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

    private fun getMap() {
        progressBarHolder.visibility = View.VISIBLE
        val postRequest: StringRequest = @SuppressLint("SetTextI18n")
        object : StringRequest(
                Method.POST, "http://103.140.90.58:8000/getmap",
                Response.Listener { response ->
                    progressBarHolder.visibility = View.GONE
                    val jObj = JSONObject(response)
                    val status = try {
                        jObj.getInt("status")
                    } catch (e: Exception) {
                        500
                    }
                    if (status == 0) {
                        lat = try {
                            jObj.getString("lat").toString().toFloat()
                        } catch (e: Exception) {
                            Toasty.error(this, "$e", Toast.LENGTH_SHORT).show()
                            0f
                        }
                        lon = try {
                            jObj.getString("lon").toString().toFloat()
                        } catch (e: Exception) {
                            Toasty.error(this, "$e", Toast.LENGTH_SHORT).show()
                            0f
                        }
                        tvLoc.text = "lat: $lat || lon: $lon"
                        Toasty.success(this, "Sukes update lokasi bos", Toast.LENGTH_SHORT).show()
                    } else {
                        Toasty.error(
                                this,
                                "Terjadi kesalahan di server, hubungi pengembang! code: $status",
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }

                },
                Response.ErrorListener {
                    progressBarHolder.visibility = View.GONE
                    Toasty.error(this, "Terjadi kesalahan koneksi", Toast.LENGTH_SHORT).show()
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                        HashMap()
                params["idkey"] = id
                return params
            }
            override fun getHeaders(): Map<String, String>? {
                val headers: MutableMap<String, String> = HashMap()
                headers["accept"] = "application/json"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Log.d("postControl", postRequest.toString())

        val queue = Volley.newRequestQueue(this)
        queue.cache.clear()
        queue.add(postRequest)
    }


    // Post Request For JSONObject
    @SuppressLint("SetTextI18n")
    fun postData() {
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val `object` = JSONObject()
        try {
            `object`.put("idkey", id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // Enter the correct url for your api service site
        val url = "http://103.140.90.58:8000/getmap"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, `object`,
                { response -> tvLoc.text = "String Response : $response" }) { tvLoc.text = "Error getting response" }
        Log.d("postControl", jsonObjectRequest.toString())
        requestQueue.add(jsonObjectRequest)
    }

    class okhttp(tv: TextView) : AsyncTask<Void, Void, String>() {
        val tvLoc = tv
        val client = OkHttpClient().newBuilder()
            .build()
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("idkey", "4574214")
            .build()
        val request: okhttp3.Request = okhttp3.Request.Builder()
            .url("http://103.140.90.58:8000/getmap")
            .method("POST", requestBody)
            .addHeader("Accept", "application/json")
            .build()
        val response: okhttp3.Response = client.newCall(request).execute()

        override fun doInBackground(vararg params: Void?): String? {
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)
        }
    }

    class CURL(tv: TextView) : AsyncTask<Void, Void, String>() {
        val tv = tv
        var returnValue: Int? = null
        val command = "curl -X POST http://103.140.90.58:8000/getmap -H  accept: application/json -H  Content-Type: null -d {idkey:4574214}"
        val process: Process = Runtime.getRuntime().exec(command)

        override fun doInBackground(vararg params: Void?): String? {
            process.inputStream.read()
            process.waitFor()
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            tv.text = "String Response : $result"
            process.inputStream.close()
            returnValue = process.exitValue()
            super.onPostExecute(result)
        }
    }


}