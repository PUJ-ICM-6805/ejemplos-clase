package com.example.activities_2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.activities_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Se agrega una variable entera en la clase MainActivity para guardar la cantidad de clics:
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        // Llama al método inflate() estático incluido en la clase de vinculación generada. Esto crea una instancia de la clase de vinculación para la actividad que se usará.
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        // Pasa la vista raíz a setContentView() para que sea la vista activa en la pantalla.
        setContentView(view)

        // Se incrementa el contador de clics en el listener del botón y se muestra la información en un log:
        binding.boton1.setOnClickListener {
            clickCount++
            Log.i("MainActivity", "Número de clics: $clickCount")
        }

        // Se agrega un listener para el segundo botón y crea un Intent para lanzar la nueva actividad.
        // El dato del campo de texto se envía como un extra o con un objeto Bundle
        binding.boton2.setOnClickListener {
            val text = binding.text1.text.toString()
            val intent = Intent(applicationContext, Activity2::class.java)
            intent.putExtra("text", text)
            startActivity(intent)
        }
    }
}