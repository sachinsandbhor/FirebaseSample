package com.sachinsandbhor.firebasesample

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val DEFAULT_MSG_LENGTH_LIMIT = 10000


    lateinit var chatAdapter: ChatAdapter

    // Firebase components
    private val mFirebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val mDatabaseReference: DatabaseReference by lazy { mFirebaseDatabase.getReference().child("messages") }
    private val mFirebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    lateinit var mChildEventListener: ChildEventListener

    var mUsername: String = "anonymus"
    val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUi()
        setupAuthStateListener()
    }

    private fun attachDatabaseReadListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val friendlyMessage = dataSnapshot.getValue(FriendlyMessage::class.java)
                chatAdapter.add(friendlyMessage!!)
                chatAdapter.addUsername(mUsername)
                messageListView.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        mDatabaseReference.addChildEventListener(childEventListener)
    }

    private fun detachDatabaseReadListener() {
        if(::mChildEventListener.isInitialized) {
            mDatabaseReference.removeEventListener(mChildEventListener)
        }
    }

    private fun setupAuthStateListener() {
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                onSignedInInitialize(user.getDisplayName());
                Toast.makeText(this@MainActivity, "You're now signed in. Welcome to FriendlyChat.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // User is signed out
                onSignedOutCleanup();
                val provider = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.EmailBuilder().build()
                )
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(provider)
                        .build(),
                    RC_SIGN_IN
                )
            }
        }

    }

    private fun onSignedOutCleanup() {
        mUsername = ""
        chatAdapter.clear()
    }

    private fun onSignedInInitialize(displayName: String?) {
        mUsername = displayName!!
        attachDatabaseReadListener()
    }

    private fun setupUi() {
        chatAdapter = ChatAdapter()
        messageListView.layoutManager = LinearLayoutManager(this)
        messageListView.adapter = chatAdapter

        progressBar.visibility = View.INVISIBLE

        photoPickerButton.setOnClickListener(View.OnClickListener {
            // TODO: Fire an intent to show an image picker
        })

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length > 0) {
                    sendButton.setEnabled(true)
                } else {
                    sendButton.setEnabled(false)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        messageEditText.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)))

        // Send button sends a message and clears the EditText
        sendButton.setOnClickListener(View.OnClickListener {
            var friendlyMessage = FriendlyMessage(messageEditText.text.toString().trim(), mUsername, "")
            mDatabaseReference.push().setValue(friendlyMessage)
            // Clear input box
            messageEditText.setText("")
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
        detachDatabaseReadListener()
        chatAdapter.clear()
    }

}
