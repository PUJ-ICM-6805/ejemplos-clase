package com.icm2330.ServicesNotifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.icm2330.ServicesNotifications.databinding.ActivityHomeBinding
import com.icm2330.ServicesNotifications.services.BasicWorkManagerService

class HomeActivity : AppCompatActivity() {

    lateinit var greetingText: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        greetingText = binding.greeting

        // Initialize Firebase Auth
        mAuth = Firebase.auth

        val user = mAuth.currentUser
        if (user != null) {
            greetingText.text = String.format("Hola %s. estás autenticado.", user.email)
        }

        binding.peopleBtn.setOnClickListener {
            goToPeopleActivity()
        }

        binding.chatBtn.setOnClickListener {
            goToChatActivity()
        }
    }

    override fun onStart() {
        super.onStart()

        // Stop works by tag
        WorkManager.getInstance(this).cancelAllWorkByTag("Counting")
        // Enqueue the work to wait for 10 seconds
        val context = this // Assuming you are in an activity, so 'this' is the context
        val milliseconds = 10000 // Define the number of milliseconds you want

        // Enqueue the work to wait for 10 seconds
        val workRequest = OneTimeWorkRequestBuilder<BasicWorkManagerService>()
            .setInputData(workDataOf("milliSeconds" to milliseconds))
            .addTag("Counting")
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuLogOut -> {
                mAuth.signOut()
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    fun goToPeopleActivity() {
        val intent = Intent(baseContext, PeopleActivity::class.java)
        startActivity(intent)
    }


    fun goToChatActivity() {
        val intent = Intent(baseContext, ChatActivity::class.java)
        startActivity(intent)
    }


    fun simpleRESTVolleyRequest() {
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://restcountries.com/v3.1/"
        val path = "name/colombia"
        val query = "?fields=capital"
        StringRequest(url + path + query,
            { response ->
                Log.i("Volley", response)
            },
            { error: VolleyError? ->
                Log.e("Volley", error.toString())
            }
        )
    }


}