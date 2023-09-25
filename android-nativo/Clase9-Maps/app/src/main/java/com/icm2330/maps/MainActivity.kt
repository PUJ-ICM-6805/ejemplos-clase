package com.icm2330.maps

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.icm2330.maps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        binding.buttonNYC.setOnClickListener {
            val intent = Intent(applicationContext, MapsNYCActivity::class.java)
            startActivity(intent)
        }

        binding.buttonBOG.setOnClickListener {
            val intent = Intent(applicationContext, MapsBOGActivity::class.java)
            startActivity(intent)
        }
    }

}