package com.example.tea

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tea.adapters.EventAdapter
import com.example.tea.databinding.ActivityEventBinding
import com.example.tea.user.Database
import com.example.tea.user.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventActivity : AppCompatActivity() {
    private lateinit var eventAdapter: EventAdapter
    private lateinit var binding: ActivityEventBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = db.collection(REF).orderBy("timestamp")
        val options = FirestoreRecyclerOptions.Builder<Event>()
            .setQuery(query, Event::class.java)
            .setLifecycleOwner(this@EventActivity).build()
        eventAdapter = EventAdapter(options)
        binding.rvEventItems.adapter = eventAdapter
        binding.rvEventItems.layoutManager = LinearLayoutManager(this)

        binding.btnAddEvent.setOnClickListener(addEventListener)
        binding.btnDeleteDoneEvents.setOnClickListener(deleteEventListener)
    }

    private val addEventListener = View.OnClickListener {
        val eventTitle = binding.etEventTitle.text.toString()
        if(eventTitle.isNotEmpty()) {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"))
            val event = Event(title=eventTitle, timestamp=timestamp)
            db.collection(REF)
                .document(timestamp)
                .set(event)
                .addOnSuccessListener { _ ->
                    Log.d(TAG, "DocumentSnapshot added with ID: $timestamp")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            binding.etEventTitle.text.clear()
        }
    }

    private val deleteEventListener = View.OnClickListener {
        val nDeleted = eventAdapter.deleteCheckedItems()
        if (nDeleted > 0){
            Toast.makeText(this@EventActivity,
                "Deleted $nDeleted item${if (nDeleted > 1) "s" else ""}",
                Toast.LENGTH_SHORT).show()
        }
        testDb()
    }


    private fun testDb(){   // TODO: make TEST out of it
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null){
            val user = User(uid)
            val manager = user.userManager
            manager.makeSampleUser()
            manager.addUser()
            val userDb = User(uid)
            val manager2 = userDb.userManager
            manager2.getUserData { userData: Database.Users.UserData? ->
                if(userData != null){
                    manager2.updateUserFields(userData)
                    Toast.makeText(this@EventActivity, "nick: " + userDb.nickname, Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@EventActivity, "events: " + userDb.eventManager.getUserEvents().toString(), Toast.LENGTH_LONG)
                        .show()    // todo: make async call (events are updated asynchronously)
                    Toast.makeText(this@EventActivity, "friends: " + userDb.friendManager.getUserFriends().toString(), Toast.LENGTH_LONG).show()
                    Toast.makeText(this@EventActivity, "invs: " + userDb.invitationManager.getEventInvitations().toString(), Toast.LENGTH_LONG).show()
                }
            }
            userDb.eventManager.getEvent("5hY21038306711"){
                if(it != null){
                    Toast.makeText(this@EventActivity, "EVENT: $it",Toast.LENGTH_LONG).show()
                }
            }
            val eid = user.eventManager.addEvent(user.eventManager.getSampleEvent())
            Toast.makeText(this@EventActivity, "Added event $eid to DB", Toast.LENGTH_SHORT).show()

            user.eventManager.updateEvent("event-id", mapOf("lat" to 44))
            user.eventManager.getEvent("event-id") { event ->
                if (event != null) {
                    Toast.makeText(this@EventActivity, event.toString(), Toast.LENGTH_LONG).show()
                } else{
                    Toast.makeText(this@EventActivity, "Warning: Missing event", Toast.LENGTH_SHORT).show()
                }
            }
            user.eventManager.deleteEvent("0419175610334")
        }
    }

    companion object {
        const val TAG = "EventActivity"
        const val REF = "events"
    }
}