package com.example.camera_gallery

import android.content.Intent
import android.graphics.Camera
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.camera_gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            val intent = Intent(applicationContext, CameraIntent::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            val intent = Intent(applicationContext, CameraX::class.java)
            startActivity(intent)
        }

    }
}