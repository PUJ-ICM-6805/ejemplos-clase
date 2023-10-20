package com.icm2330.ServicesNotifications

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.icm2330.ServicesNotifications.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.name
    private val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEdit = binding.loginEmail
        passEdit = binding.loginPass

        // Initialize Firebase Auth
        mAuth = Firebase.auth

        binding.loginBtn.setOnClickListener {
            login()
        }

        binding.signupBtn.setOnClickListener {
            signUp()
        }

        binding.forgotpassBtn.setOnClickListener {
            forgotPassword()
        }

    }


    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(baseContext, HomeActivity::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            emailEdit.setText("")
            passEdit.setText("")
        }
    }


    private fun validateForm(): Boolean {
        var valid = true
        val email = emailEdit.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailEdit.error = "Required"
            valid = false
        } else {
            emailEdit.error = null
        }
        val password = passEdit.text.toString()
        if (TextUtils.isEmpty(password)) {
            passEdit.error = "Required"
            valid = false
        } else {
            passEdit.error = null
        }
        return valid
    }



    private fun signInUser(email: String, password: String) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI
                        Log.d(TAG, "signInWithEmail: Success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If Sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: Failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }


    private fun isEmailValid(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }


    private fun login() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!isEmailValid(email)) {
            Toast.makeText(this@LoginActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        signInUser(email, pass)
    }


    private fun signUp() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!isEmailValid(email)) {
            Toast.makeText(this@LoginActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                Toast.makeText(
                    this@LoginActivity,
                    String.format("The user %s is successfully registered", user!!.email),
                    Toast.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener(this) { e ->
            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show() }
    }


    private fun forgotPassword() {
        val email = emailEdit.text.toString()
        if (isEmailValid(email)) {
            Toast.makeText(
                this@LoginActivity,
                "Email is not a valid format",
                Toast.LENGTH_SHORT)
                .show()
            return
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
            Toast.makeText(
                this@LoginActivity,
                "Email instructions hace been sent, please check your email",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}