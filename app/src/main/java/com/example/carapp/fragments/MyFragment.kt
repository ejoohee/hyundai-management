package com.example.carapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import com.example.carapp.R
import com.example.carapp.activities.IntroActivity
import com.example.carapp.activities.PostListActivity
import com.example.carapp.activities.UpdateCarActivity
import com.example.carapp.models.Car
import com.example.carapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MyFragment : Fragment() {
    private val carList = listOf(
        // 수소/전기차
        Car("1", "캐스퍼 일렉트릭", 2990.0, 0.0, 0, "전기차", R.drawable.casper_ev),
        Car("2", "ST1", 5595.0, 0.0, 0, "전기차", R.drawable.st1_24lc),
        Car("3", "넥쏘", 6950.0, 0.0, 0, "수소", R.drawable.nexo_24my),
        Car("4", "코나 Electric", 4142.0, 0.0, 0, "전기차", R.drawable.kona_electric_24my),
        Car("5", "아이오닉 6", 4695.0, 0.0, 0, "수소", R.drawable.ioniq6_24my),
        Car("6", "포터 II Electric", 4395.0, 0.0, 0, "전기차", R.drawable.porter2_electric),

        // N
        Car("7", "아이오닉 5 N", 7700.0, 0.0, 0, "N", R.drawable.ioniq5_n_25my),
        Car("8", "아반떼 N", 3360.0, 12.5, 1998, "N", R.drawable.avante_n_25my),

        // 승용
        Car("9", "쏘나타 디 엣지", 2831.0, 14.3, 1598, "승용", R.drawable.sonata_the_edge_25my),
        Car("10", "아반떼", 1994.0, 15.3, 1598, "승용", R.drawable.avante_25my),
        Car("11", "아반떼 Hybrid", 2485.0, 20.1, 1598, "승용", R.drawable.avante_hybrid_25my),
        Car("12", "그랜저", 3768.0, 10.1, 2497, "승용", R.drawable.grandeur_25my),
        Car("13", "그랜저 Hybrid", 4291.0, 18.0, 1598, "승용", R.drawable.grandeur_hybrid_25my),

        // SUV
        Car("14", "더 뉴 캐스퍼", 1460.0, 14.2, 998, "SUV", R.drawable.casper_25my),
        Car("15", "투싼", 2771.0, 13.5, 1598, "SUV", R.drawable.tucson_25my),
        Car("16", "투싼 Hybrid", 3213.0, 19.5, 1598, "SUV", R.drawable.tucson_hybrid_25my),
        Car("17", "베뉴", 2146.0, 13.8, 1591, "SUV", R.drawable.venue_23my),
        Car("18", "코나", 2446.0, 14.8, 1598, "SUV", R.drawable.kona_24my),
        Car("19", "싼타페", 3546.0, 11.8, 2199, "SUV", R.drawable.santafe_25my),
        Car("20", "팰리세이드", 3896.0, 9.5, 3498, "SUV", R.drawable.palisade_24my)
    )

    // Firestore 인스턴스 초기화
    val db: FirebaseFirestore = Firebase.firestore
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        val textViewLogout = view.findViewById<TextView>(R.id.textViewLogout)
        textViewLogout.setOnClickListener {
            // Firebase 인증 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 인트로 화면으로 이동
            val intent = Intent(requireContext(), IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 이전 액티비티 제거
            startActivity(intent)
        }


        // 차 정보 이미지뷰
        val imageViewCar = view.findViewById<ImageView>(R.id.imageViewCar)
        val textViewNickname = view.findViewById<TextView>(R.id.textViewNickname)

        val carNumberTextView = view.findViewById<TextView>(R.id.carNumberTextview)

        //차량 수정 버튼
        val editTextView = view.findViewById<TextView>(R.id.editTextView)

        //문의글 조회/수정/삭제
        val postLayout = view.findViewById<LinearLayout>(R.id.PostLayout)
        // 내 정보 불러오기
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid
            getUser(currentUserId) { user ->
                if (user != null) {
                    textViewNickname.text = user.nickname
                    carNumberTextView.text =user.carNumber
                    // 사용자의 carId에 맞는 차 이미지 가져오기
                    val carId = user.carId // 사용자의 carId를 가져옴
                    val car = carList.find { it.id == carId } // carList에서 해당 carId에 맞는 차 찾기
                    if (car != null) {
                        imageViewCar.setImageResource(car.imageResId) // 해당 차의 이미지 리소스를 ImageView에 설정
                    }
                } else {
                    // 사용자 정보를 가져오는 데 실패했을 때 처리
                    println("User not found or error occurred.")
                }
            }
        }

        editTextView.setOnClickListener {
            val intent = Intent(requireContext(), UpdateCarActivity::class.java)
            startActivity(intent)
            loadUserData()
        }

        postLayout.setOnClickListener {
            val intent = Intent(requireContext(), PostListActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        db.collection("users")
            .document(userId) // 사용자 ID로 문서 가져오기
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user) // 변환한 User 객체를 callback으로 전달
                } else {
                    callback(null) // 해당하는 문서가 없을 경우 null 전달
                }
            }
            .addOnFailureListener { exception ->
                // 에러 처리
                println("Error getting document: $exception")
                callback(null) // 에러 발생 시 null 전달
            }
    }


    fun loadUserData() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // 업데이트된 user 데이터 읽어오기
                        val carId = document.getString("carId")
                        val carNumber = document.getString("carNumber")

                        // 데이터 업데이트 후 UI 갱신 (예: 프래그먼트나 액티비티에 반영)
                        updateUI(carId, carNumber)
                    } else {
                        Log.d("UpdateCarActivity", "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("UpdateCarActivity", "Error getting document", e)
                }
        }
    }

    // UI 갱신 메서드 (예: TextView에 carId와 carNumber를 반영)
    fun updateUI(carId: String?, carNumber: String?) {
        val car = carList.find { it.id == carId }
        view?.findViewById<TextView>(R.id.carNumberTextview)?.text = carNumber
        if (car != null) {
            view?.findViewById<ImageView>(R.id.imageViewCar)?.setImageResource(car.imageResId) // 해당 차의 이미지 리소스를 ImageView에 설정
        }
    }

    // Fragment가 다시 화면에 보일 때마다 데이터 새로고침
    override fun onResume() {
        super.onResume()
        loadUserData()
    }
}