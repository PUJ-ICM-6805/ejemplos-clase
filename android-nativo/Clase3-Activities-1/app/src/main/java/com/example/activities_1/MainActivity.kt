package com.example.activities_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// Importamos las clases de los paquetes necesarios
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Asignación de las vistas de la UI a los objetos
        val mButton: Button = findViewById(R.id.button)
        val mEditText: EditText = findViewById(R.id.editText)

        // Listener de clic para el botón
        mButton.setOnClickListener {
            val nombre = mEditText.text.toString()
            val saludo = getString(R.string.saludo, nombre)
            Toast.makeText(applicationContext, saludo, Toast.LENGTH_SHORT).show()
        }
    }
}