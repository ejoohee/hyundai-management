package com.example.carapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carapp.R
import com.example.carapp.models.Car
import com.example.carapp.models.User
import com.example.carapp.models.UserCar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    lateinit var car: Car // lateinit으로 선언하여 초기화 전에 접근 가능

    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
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

        // 컴포넌트 선언
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPW)
        val editTextNickname = findViewById<EditText>(R.id.editTextNick)
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

        // "가입하기 버튼"
        textViewComplete.setOnClickListener {
            val user = User(
                "",
                editTextEmail.text.toString(),
                editTextNickname.text.toString(),
                editTextCarNumber.text.toString(),
                car?.id ?: ""
            )

            Log.d("User", user.toString())
            signUp(user, editTextPassword.text.toString())
        }
    }

    // 회원가입 메서드
    private fun signUp(user: User, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공 시, Firebase Authentication에서 생성된 UID 가져오기
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // User 객체에 ID 설정
                        user.id = userId // id만 입력해준다.
                        // Firestore에 사용자 데이터 저장
                        saveUserData(user)
                    }
                } else {
                    // 에러 처리
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Firestore에 사용자 데이터 저장
    private fun saveUserData(user: User) {


        // 사용자 UID를 Firestore 문서 ID로 사용하여 저장
        firestore.collection("users")
            .document(user.id) // UID를 문서 ID로 사용
            .set(user) // 사용자 객체를 저장
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User data successfully written!")

                saveUserCar(user.id, user.carId)
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error writing document", e)
            }
    }

    private fun saveUserCar(userId: String, carId: String) {
        val userCar = UserCar(
            userId = userId,
            carId = carId,
            temperature = "22.0"
        )

        firestore.collection("UserCar")
            .add(userCar) // 사용자 객체를 저장
            .addOnSuccessListener {
                Log.d("SignUpActivity", "UserCar data successfully written!")

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error writing document", e)
            }
    }

    // SignUpActivity에서 onActivityResult 오버라이드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAR_LIST && resultCode == RESULT_OK) {
            // CarListActivity에서 선택된 자동차 정보 받기
            val selectedCar = data?.getParcelableExtra<Car>("car")

            // 선택된 자동차 정보가 있으면 UI에 반영
            selectedCar?.let {
                findViewById<TextView>(R.id.carNameTextView).text = it.name
                findViewById<ImageView>(R.id.carImageView).setImageResource(it.imageResId)
                findViewById<LinearLayout>(R.id.carLayout).visibility = View.VISIBLE

                this.car = selectedCar
            }


        }
    }

    companion object {
        const val REQUEST_CODE_CAR_LIST = 1001
    }
}