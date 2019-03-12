package com.sachinsandbhor.firebasesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val DEFAULT_MSG_LENGTH_LIMIT = 10000


    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var chatAdapter: ChatAdapter

    val mUsername: String = "anonymus"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.getReference().child("messages")
        setupUi()
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

        mDatabaseReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, p0.message, Toast.LENGTH_LONG).show()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val friendlyMessage = dataSnapshot.getValue(FriendlyMessage::class.java)
                chatAdapter.add(friendlyMessage!!)
                messageListView.smoothScrollToPosition(chatAdapter.itemCount -1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}
