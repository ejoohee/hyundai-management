package com.example.carapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.carapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostUpdateActivity : AppCompatActivity() {
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val CAMERA_PERMISSION_CODE = 102
    private var imageUri: Uri? = null
    private var photoUri: Uri? = null
    private var postId: String? = null // 포스트 ID

    private lateinit var imageView: ImageView
    private lateinit var loadingOverlay: FrameLayout // 로딩 오버레이

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById<ImageView>(R.id.imageView)

        val imageUrl = intent.getStringExtra("image")
        if (!imageUrl.isNullOrEmpty()) {
            // Glide를 사용하여 이미지 로드 (이미지 URL 처리)
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val contentEditText = findViewById<EditText>(R.id.contentEditText)

        // 전달된 초기값 설정
        titleEditText.setText(intent.getStringExtra("title"))
        contentEditText.setText(intent.getStringExtra("content"))

        // 로딩 오버레이
        loadingOverlay = findViewById(R.id.loadingOverlay)

        imageView = findViewById<ImageView>(R.id.imageView)
        val btnCamera = findViewById<TextView>(R.id.btnCamera)
        val btnAlbum = findViewById<TextView>(R.id.btnAlbum)

        // 포스트 ID 가져오기 (이전 화면에서 전달된 데이터)
        postId = intent.getStringExtra("postId")

        // 앨범 선택
        btnAlbum.setOnClickListener {
            openGallery()
        }

        // 카메라 선택
        btnCamera.setOnClickListener {
            checkCameraPermission()
        }
        val completeButton = findViewById<TextView>(R.id.textViewComplete)
        completeButton.setOnClickListener {
            Log.d("PostUpdateActivity", "textViewComplete clicked")
            if (postId != null) {
                imageUri?.let { it1 -> updatePost(postId!!, it1) }
            } else {
                Toast.makeText(this, "포스트 ID가 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
        // Toolbar 설정
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayShowTitleEnabled(false) // 제목 숨김

        toolbar.setNavigationOnClickListener {
            onBackPressed() // 뒤로가기 버튼 동작
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updatePost(postId: String, uri: Uri) {
        showLoading(true)

        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
        val uploadTask: UploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                imageUri = null
                showLoading(false)

                // 사용자 이메일 가져오기
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null) {
                    // 포스트 정보와 함께 이미지를 Firestore에 업데이트
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // 올바른 SimpleDateFormat 객체 생성
                    val currentDate = dateFormat.format(Date())  // 오늘 날짜를 "yyyy-MM-dd" 형식으로 포맷팅

                    val postData = mapOf(
                        "title" to findViewById<EditText>(R.id.titleEditText).text.toString(),
                        "content" to findViewById<EditText>(R.id.contentEditText).text.toString(),
                        "image" to downloadUrl.toString(),
                        "userId" to userId,
                        "date" to currentDate
                    )

                    // Firestore에서 해당 포스트 업데이트
                    val db = Firebase.firestore
                    db.collection("Post").document(postId)
                        .update(postData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "포스트가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, PostListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "포스트 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                } else {
                    Toast.makeText(this, "사용자 인증 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(this, "업로드 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 요청 메서드 추가
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val photoFile = File(getExternalFilesDir(null), "photo_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        } else {
            Toast.makeText(this, "카메라를 사용할 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    imageView.setImageURI(photoUri)
                    imageUri = photoUri
                }
                REQUEST_CODE_GALLERY -> {
                    imageUri = data?.data
                    imageView.setImageURI(imageUri)
                }
            }
        }
    }
}