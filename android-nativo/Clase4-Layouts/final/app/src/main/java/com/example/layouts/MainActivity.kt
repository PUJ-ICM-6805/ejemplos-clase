package com.example.layouts

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.example.layouts.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val COUNTRIES_FILE = "paises.json"
    }

    lateinit var countrySpinner: Spinner
    lateinit var countryList: ListView
    lateinit var countries: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        countrySpinner = binding.spinner
        countryList = binding.countryList

        // Se crea una lista vacía de "cadenas de texto" llamada "countriesArray" que se utilizará para almacenar los nombres de los países.
        // A partir del objeto JSON devuelto por la función "loadCountriesByJson()", se obtiene un array JSON llamado "countries" que contiene los detalles de cada país.
        // Se itera a través de cada elemento del array JSON "countries" y se agrega el nombre de cada país en la lista "countriesArray".
        val countriesArray: MutableList<String> = ArrayList()
        try {
            val jsonFile = loadCountriesByJson()
            countries = jsonFile.getJSONArray("Countries")
            for (i in 0 until countries.length()) {
                countriesArray.add(countries.getJSONObject(i).getString("Name"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


        // Spinner
        // Con el adapter se llena el spinner con los nombres de cada país utilizando la lista "countriesArray"
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, countriesArray)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter

        // Listener para el Spinner
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Aquí se obtiene el valor del item seleccionado
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Se muestra en un Toast el valor seleccionado
                Toast.makeText(
                    baseContext,
                    String.format("País seleccionado: %S", selectedItem),
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Este método se llama cuando no se ha seleccionado ningún item en el spinner
            }
        }

        // ListView
        // Con el adapter se llena el ListView con los nombres de cada país utilizando la lista "countriesArray"
        val adapterListView = ArrayAdapter(this, R.layout.simple_list_item_1, countriesArray)
        countryList.adapter = adapterListView

        // Listener para el ListView
        countryList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id -> // Se llama a la actividad 2 y se envían los detalles del país seleccionado utilizando la posición y el array JSON "countries"
                val intent = Intent(applicationContext, Activity2::class.java)
                try {
                    intent.putExtra(
                        "paisSeleccionado",
                        countries.getJSONObject(position).toString()
                    )
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                startActivity(intent)
            }
    }


    fun loadJSONFromAsset(assetName: String): String {
        val json: String
        json = try {
            val inputStream = this.assets.open(assetName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            ""
        }
        return json
    }

    @Throws(JSONException::class)
    fun loadCountriesByJson(): JSONObject {
        return JSONObject(loadJSONFromAsset(COUNTRIES_FILE))
    }

}