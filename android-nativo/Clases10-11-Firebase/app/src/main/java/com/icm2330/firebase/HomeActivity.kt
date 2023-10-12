package com.icm2330.firebase

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.icm2330.firebase.databinding.ActivityHomeBinding

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
}