package com.sidewinderz0ne.pedamob

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.dmoral.toasty.Toasty
import me.dm7.barcodescanner.zxing.ZXingScannerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler  {
    private var qr = -1

    private lateinit var mScannerView: ZXingScannerView
    private var cameraIsStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkGeneralPermissions()
        btQR.setOnClickListener {
            buttonHandler()
        }
    }

    override fun handleResult(rawResult: Result?) {
        Toasty.success(this, "$rawResult", Toasty.LENGTH_SHORT).show()
        try {
            val pref = PrefManager(this)
            pref.id = rawResult.toString().toInt()
            val intent =
                    Intent(this@MainActivity, ControlActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Toasty.error(this, "Error: QR Code bermasalah -- $e", Toasty.LENGTH_SHORT).show()
        }
    }

    private fun initScannerView() {
        mScannerView = ZXingScannerView(this)
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        qr_frame.addView(mScannerView)
    }

    override fun onStart() {
        doRequestPermission()
        super.onStart()
    }

    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                initScannerView()
            }
            else -> {
            }
        }
    }

    private fun buttonHandler() {
        if (!cameraIsStarted) {
            initScannerView()
            mScannerView.startCamera()
            cameraIsStarted = true
        } else {
            mScannerView.resumeCameraPreview(this)
        }
    }

    private fun checkGeneralPermissions(){
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    }
                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        if (shouldProvideRationale){
                            //TO DO
                        }
                    }
                }).check()

    }



}