package com.example.carapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carapp.R
import com.example.carapp.activities.SignUpActivity.Companion.REQUEST_CODE_CAR_LIST
import com.example.carapp.models.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateCarActivity : AppCompatActivity() {
    lateinit var car: Car

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_car)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar 설정
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayShowTitleEnabled(false) // 제목 숨김

        toolbar.setNavigationOnClickListener {
            onBackPressed() // 뒤로가기 버튼 동작
        }

        val editTextCarNumber = findViewById<EditText>(R.id.TextCarNumber)

        val addLayout = findViewById<LinearLayout>(R.id.addLayout)
        val addImgView = findViewById<ImageView>(R.id.addImgView)

        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)

        addImgView.setOnClickListener {
            val intent = Intent(this, CarListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CAR_LIST)
        }

        addLayout.setOnClickListener {
            val intent = Intent(this, CarListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CAR_LIST)
        }

        textViewComplete.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val currentUserId = currentUser.uid
                // car 객체가 null이 아닐 경우에만 처리
                val carId = car.id.takeIf { it.isNotEmpty() } ?: return@setOnClickListener
                val carNumber = editTextCarNumber.text.toString()

                updateUserCar(carId)
                updateUser(carId, carNumber)

                onBackPressed()
            } else {
                Log.w("UpdateCarActivity", "No current user found")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAR_LIST && resultCode == RESULT_OK) {
            val selectedCar = data?.getParcelableExtra<Car>("car")

            selectedCar?.let {
                findViewById<TextView>(R.id.carNameTextView).text = it.name
                findViewById<ImageView>(R.id.carImageView).setImageResource(it.imageResId)
                findViewById<LinearLayout>(R.id.carLayout).visibility = View.VISIBLE

                this.car = selectedCar
            }
        }
    }

    // `usercar` 컬렉션에서 carId 업데이트
    fun updateUserCar(carId: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userCarRef = db.collection("UserCar").document(currentUser.uid)
            userCarRef.update("carId", carId)
                .addOnSuccessListener {
                    Log.d("UpdateCarActivity", "Car ID successfully updated in usercar")
                }
                .addOnFailureListener { e ->
                    Log.w("UpdateCarActivity", "Error updating car ID in usercar", e)
                }
        } else {
            Log.w("UpdateCarActivity", "No current user found")
        }
    }

    // `users` 컬렉션에서 carId와 carNumber 업데이트
    fun updateUser(carId: String, carNumber: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)
            userRef.update(
                "carId", carId,  // carId 업데이트
                "carNumber", carNumber  // carNumber 업데이트
            )
                .addOnSuccessListener {
                    Log.d("UpdateCarActivity", "User data successfully updated")
                }
                .addOnFailureListener { e ->
                    Log.w("UpdateCarActivity", "Error updating user data", e)
                }
        } else {
            Log.w("UpdateCarActivity", "No current user found")
        }
    }
}
