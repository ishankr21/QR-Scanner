package com.example.qrcodescanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks  {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
         binding.cardView2.visibility= View.VISIBLE

        binding.btnScan.setOnClickListener {
             cameraTask()

        }
        binding.btnEnter.setOnClickListener {
            if(binding.edtCode.text.isNullOrBlank())
                Toast.makeText(this,"Link Empty",Toast.LENGTH_SHORT).show()
            else
            {
                val url=binding.edtCode.text.toString()
                if( URLUtil.isValidUrl(url))
                {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(url)
                    startActivity(openURL)
                }
                else
                {
                    Toast.makeText(this,"Link Not Valid!",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
    private fun hasCameraAccess(): Boolean {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)
    }
    private fun cameraTask() {

        if (hasCameraAccess()) {

            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("scan a QR code")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java

            qrScanner.initiateScan()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app can't Work without camera permissions, please provide it from settings.",
                123,
                android.Manifest.permission.CAMERA
            )
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show()
                binding.edtCode!!.setText("")
            } else {
                try {


                    binding.cardView1!!.visibility = View.VISIBLE
                    binding.cardView2!!.visibility = View.GONE
                    binding.edtCode!!.setText(result.contents.toString())
                } catch (exception: JSONException) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    binding.edtCode!!.setText("")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this,"Permission Granted!",Toast.LENGTH_LONG).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this,"Please Provide Camera Permission From Settings",Toast.LENGTH_LONG).show()
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
}