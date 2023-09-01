package com.example.camera_gallery


import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.camera_gallery.databinding.ActivityCameraXBinding
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException


class CameraX : AppCompatActivity() {

    private lateinit var binding: ActivityCameraXBinding

    private var captureButton: ImageButton? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var previewView: PreviewView? = null
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXBinding.inflate(layoutInflater)
        setContentView(binding.root)
        captureButton = binding.buttonCapture
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        previewView = binding.previewView
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun startCamera() {
        captureButton!!.setOnClickListener { v: View? -> takePicture() }
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture!!.get()
                preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                preview!!.setSurfaceProvider(previewView!!.surfaceProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture() {
        // Configurar opciones para el archivo de salida de la imagen
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(
                System.currentTimeMillis()
            ) + ".jpg"
        )
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Tomar la foto utilizando ImageCapture y mostrar un mensaje de Toast en caso de éxito o un mensaje de error en caso de fallo
        imageCapture!!.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@CameraX, "Image saved", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: ImageCaptureException) {
                    Log.e("CameraX", "Error taking picture", error)
                }
            })
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}