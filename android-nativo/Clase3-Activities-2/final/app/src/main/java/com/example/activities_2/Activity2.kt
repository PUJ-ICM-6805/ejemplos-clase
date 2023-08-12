package com.example.activities_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.activities_2.databinding.Activity2Binding

class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_2)
        val binding: Activity2Binding = Activity2Binding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        val text = intent.getStringExtra("text")
        binding.textoRecibido.text = text
    }
}