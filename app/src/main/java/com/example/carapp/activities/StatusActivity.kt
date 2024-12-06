package com.example.carapp.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.carapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CarStatusActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var powerImageView: ImageView
    private lateinit var isPowerTextView: TextView
    private lateinit var lockImageView: ImageView
    private lateinit var isLockTextView: TextView

    private var engineStatus: String = "off"
    private var lockStatus: String = "locked"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_status)

        database = FirebaseDatabase.getInstance().getReference("users/userId/car")

        powerImageView = findViewById(R.id.powerImageView)
        isPowerTextView = findViewById(R.id.isPowerTextView)
        lockImageView = findViewById(R.id.lockImageView)
        isLockTextView = findViewById(R.id.isLockTextView)

        fetchCarData()

        powerImageView.setOnClickListener { toggleEngineStatus() }
        lockImageView.setOnClickListener { toggleLockStatus() }
    }

    private fun fetchCarData() {
        database.get().addOnSuccessListener { snapshot ->
            engineStatus = snapshot.child("engineStatus").value.toString()
            lockStatus = snapshot.child("lockStatus").value.toString()

            // ui 변경
            updateEngineUI(engineStatus)
            updateLockUI(lockStatus)
        }.addOnFailureListener {
        }
    }

    private fun toggleEngineStatus() {
        engineStatus = if (engineStatus == "on") "off" else "on"
        database.child("engineStatus").setValue(engineStatus).addOnCompleteListener {
            if (it.isSuccessful) {
                updateEngineUI(engineStatus)
            }
        }
    }

    private fun toggleLockStatus() {
        lockStatus = if (lockStatus == "locked") "unlocked" else "locked"
        database.child("lockStatus").setValue(lockStatus).addOnCompleteListener {
            if (it.isSuccessful) {
                updateLockUI(lockStatus)
            }
        }
    }

    private fun updateEngineUI(status: String) {
        if (status == "on") {
            powerImageView.setImageResource(R.drawable.ic_power_on)
            isPowerTextView.text = "켜짐"
        } else {
            powerImageView.setImageResource(R.drawable.ic_power_off)
            isPowerTextView.text = "꺼짐"
        }
    }

    private fun updateLockUI(status: String) {
        if (status == "locked") {
            lockImageView.setImageResource(R.drawable.ic_lock)
            isLockTextView.text = "잠김"
        } else {
            lockImageView.setImageResource(R.drawable.ic_unlocked)
            isLockTextView.text = "열림"
        }
    }
}
