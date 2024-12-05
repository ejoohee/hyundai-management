package com.example.carapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carapp.R
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 로그인 했다면 메인으로 자동으로 이동
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 사용자가 로그인된 상태
            Log.d("IntroActivity", currentUser.toString())

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // 회원가입 버튼
        val buttonSignUP = findViewById<TextView>(R.id.buttonSignUP)
        buttonSignUP.setOnClickListener {
            //val intent = Intent(this, UploadActivity::class.java)
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼
        val buttonLogin = findViewById<TextView>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            //val intent = Intent(this, UploadActivity::class.java)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}