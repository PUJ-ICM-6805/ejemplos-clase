package com.example.layouts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Spinner
import com.example.layouts.databinding.Activity2Binding
import com.example.layouts.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject

class Activity2 : AppCompatActivity() {

    private lateinit var binding: Activity2Binding

    lateinit var myWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_2)

        binding = Activity2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se recibe el String que contiene los detalles del país seleccionado y se guarda en la variable "paisSeleccionado"
        val paisSeleccionado = intent.getStringExtra("paisSeleccionado")

        // Se crea un objeto "JSONObject" llamado "pais" para almacenar los detalles el país seleccionado
        // Se utiliza solo el nombre "Name" del país para cargar en el WebView la página de wikipedia del país
        // Aquí se podrían utilizar los otros datos del país para mostrar más información

        val pais: JSONObject
        pais = loadJSONFromString(paisSeleccionado!!)

        myWebView = binding.webView
        myWebView.webViewClient = WebViewClient()
        myWebView.loadUrl(String.format("https://en.wikipedia.org/wiki/%s", pais.getString("Name")))
    }

    fun loadJSONFromString (jsonString: String): JSONObject {
        return try {
            JSONObject(jsonString)
        } catch (ex: org.json.JSONException) {
            ex.printStackTrace()
            JSONObject()
        }
    }
}