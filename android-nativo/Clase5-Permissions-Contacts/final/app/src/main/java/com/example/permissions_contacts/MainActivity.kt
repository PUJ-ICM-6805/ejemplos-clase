package com.example.permissions_contacts

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.permissions_contacts.adapters.ContactInfoAdapter
import com.example.permissions_contacts.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Permission handler
    val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        updateUI(it)
    }

    //Contacts: Cursor parameters
    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.STARRED,
        ContactsContract.Contacts.PHOTO_URI)

    val filter = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} IS NOT NULL"
    val order = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} COLLATE NOCASE"

    lateinit var adapter: ContactInfoAdapter

    // onCreate callback method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // // El Cursor del Aadapter está en null hasta que el usuario acepte el permiso
        adapter = ContactInfoAdapter(this, null)
        binding.contactslist.adapter = adapter

        verifyPermissions(this, android.Manifest.permission.READ_CONTACTS, "El permiso es requerido para...")

    }

    // onResume callback method
    override fun onResume() {
        super.onResume()
        Log.w("Callback: ", "onResume")
    }


    // Verify permission to access contacts info
    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Mostramos un snackbar con la justificación del permiso y una vez desaparezca volvemos a solicitarlo
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }

    // Update activity view according to result of permission request
    fun updateUI(permission : Boolean) {
        if(permission){
            //granted
            Log.i("Permission: ", "Granted")
            // Assigning a cursor to init the adapter
            val cursor: Cursor? = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, filter, null, order)
            adapter.changeCursor(cursor)
        }else{
            Log.i("Permission: ", "Denied")
        }
    }
}