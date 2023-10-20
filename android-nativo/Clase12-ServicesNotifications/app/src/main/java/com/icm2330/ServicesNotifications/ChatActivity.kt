package com.icm2330.ServicesNotifications

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.icm2330.ServicesNotifications.adapters.MessageAdapter
import com.icm2330.ServicesNotifications.databinding.ActivityChatBinding
import com.icm2330.ServicesNotifications.model.DatabasePaths
import com.icm2330.ServicesNotifications.model.Message
import java.util.Date

import com.icm2330.ServicesNotifications.services.BasicWorkManagerService
import com.icm2330.ServicesNotifications.services.BasicWorkManagerServiceChat
import java.util.logging.Logger

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    companion object {
        val TAG: String = ChatActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    // Varriables for Firebase Auth
    private lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser

    // Variables for Firebase DB
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    var valueEventListener: ValueEventListener? = null

    // Local Data
    lateinit var adapter: MessageAdapter
    lateinit var sendButton: LottieAnimationView
    lateinit var messageEdit: EditText
    var messages = ArrayList<Message>()
    lateinit var messageList: ListView

    //Permission handler
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        // Initialize Adapter
        adapter = MessageAdapter(this@ChatActivity, messages)
        messageList = binding.chatMessages
        messageList.adapter = adapter

        sendButton = binding.sendButton
        messageEdit = binding.messageEdit

        // Request and verify permissions for Notifications
        verifyPermissions(this, android.Manifest.permission.POST_NOTIFICATIONS, "If notification permission is active, the app will notify you when there is a new message in the chat.")
    }


    override fun onStart() {
        super.onStart()
        currentUser = mAuth.currentUser!!
        if (currentUser == null) {
            logout()
        }
        loadMessages()

        val workRequest = OneTimeWorkRequestBuilder<BasicWorkManagerServiceChat>()
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }


    override fun onPause() {
        super.onPause()
        valueEventListener?.let { myRef.removeEventListener(it) }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuLogOut -> {
                logout()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }


    fun loadMessages() {
        myRef = database.getReference(DatabasePaths.CHAT)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages.clear()
                for (snapshot in dataSnapshot.children) {
                    val tmpMsg = snapshot.getValue(Message::class.java)
                    messages.add(tmpMsg!!)
                }
                adapter.notifyDataSetChanged()
                messageList.post { messageList.setSelection(messages.size - 1) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Error in query", databaseError.toException())
            }
        })
    }


    fun sendMessage(view: View?) {
        val message = messageEdit.text.toString()
        if (message.isNotEmpty()) {
            messageEdit.text.clear()
            val key = myRef.push().key
            val msgToSend = Message(key, message, currentUser.email, Date())
            myRef = database.getReference(DatabasePaths.CHAT + key)
            myRef.setValue(msgToSend)
            sendButton.playAnimation()
        } else {
            Toast.makeText(this@ChatActivity, "Message cannot be empty.", Toast.LENGTH_SHORT).show()
        }
    }


    // Verify permission to access contacts info
    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "I already have the notification permissions 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
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
            logger.info("Permission granted")
        }else{
            logger.warning("Permission denied")
        }
    }


    private fun logout() {
        mAuth.signOut()
        val intent = Intent(this@ChatActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}