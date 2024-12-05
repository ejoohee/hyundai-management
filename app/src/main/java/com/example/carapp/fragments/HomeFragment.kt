package com.example.carapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.carapp.R
import com.example.carapp.models.Car
import com.example.carapp.models.User
import com.example.carapp.models.UserCar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text

class HomeFragment : Fragment() {
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
    private lateinit var statusLockTextView:TextView
    private lateinit var statusFanTextView:TextView
    private lateinit var statusDoorTextView:TextView

    // Firestore 인스턴스 초기화
    val db: FirebaseFirestore = Firebase.firestore

    private val handler = Handler(Looper.getMainLooper())
    private var currentCardIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //내 차 정보
        val carTextView = view.findViewById<TextView>(R.id.carTextView)
        val carImageView = view.findViewById<ImageView>(R.id.carImageView)

        //카드 배너
        val bannerScroll = view.findViewById<HorizontalScrollView>(R.id.bannerScroll)
        val bannerLayout = view.findViewById<LinearLayout>(R.id.bannerLayout)
        val card1 = view.findViewById<ImageView>(R.id.card1)
        val card2 = view.findViewById<ImageView>(R.id.card2)
        val card3 = view.findViewById<ImageView>(R.id.card3)
        val card4 = view.findViewById<ImageView>(R.id.card4)
        val card5 = view.findViewById<ImageView>(R.id.card5)

        val cards = listOf(card1, card2, card3, card4, card5)

        // 자동 스크롤
        startAutoScroll(bannerScroll, cards)

        //power, connect, 주행거리 정보
        val isPowerTextView = view.findViewById<TextView>(R.id.isPowerTextView)
        val connectTextView = view.findViewById<TextView>(R.id.connectTextView)
        val distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
        
        //차량제어
        //잠금 버튼
        val lockLayout = view.findViewById<LinearLayout>(R.id.lockLayout)
        val powerLayout = view.findViewById<LinearLayout>(R.id.powerLayout)
        val openLayout = view.findViewById<LinearLayout>(R.id.openLayout)

        val lockTextView = view.findViewById<TextView>(R.id.lockTextView)
        val powerTextView = view.findViewById<TextView>(R.id.powerTextView)
        val openTextView = view.findViewById<TextView>(R.id.openTextView)

        //내 차량 상태
        val statusFanLayout = view.findViewById<LinearLayout>(R.id.statusFanLayout)
        statusLockTextView = view.findViewById<TextView>(R.id.statusLockTextView)
        statusFanTextView = view.findViewById<TextView>(R.id.statusFanTextView)
        statusDoorTextView = view.findViewById<TextView>(R.id.statusDoorTextView)

        //db의 userCar
        val userCar:UserCar

        // 내 정보 불러오기
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val currentUserId = currentUser.uid
            // userCar 정보 가져오기
            getUserCar(currentUserId) { userCar ->
                if (userCar != null) {
                    // userCar의 carId를 기반으로 carList에서 차 정보 찾기
                    val car = carList.find { it.id == userCar.carId }
                    if (car != null) {
                        carTextView.text = car.name // 차 이름 설정
                        carImageView.setImageResource(car.imageResId) // 차 이미지 설정
                        if(userCar.isPower) {
                            isPowerTextView.text = "켜짐"
                        }else{
                            isPowerTextView.text = "꺼짐"
                        }

                        if(userCar.isConnect) {
                            connectTextView.text = "connected"
                        }else{
                            connectTextView.text = "unconnected"
                        }

                        if(userCar.isLock){
                            statusLockTextView.text = "잠김"

                        }else{
                            statusLockTextView.text = "닫힘"
                        }
                        
                        if(userCar.isFan) {
                            statusFanTextView.text = "켜짐"
                        }else{
                            statusFanTextView.text = "꺼짐"
                        }

                        if(userCar.isOpen){
                            statusDoorTextView.text = "열림"
                        }else{
                            statusDoorTextView.text = "닫힘"
                        }
                        

                        distanceTextView.text = userCar.distance + " km"

                    } else {
                        carTextView.text = "차 정보를 찾을 수 없습니다."
                    }
                } else {
                    carTextView.text = "UserCar 정보를 불러올 수 없습니다."
                }
            }

        }

        lockLayout.setOnClickListener {
            updateIsLockUserCar(false)
        }

        openLayout.setOnClickListener {
            updateIsLockUserCar(true)
        }

        statusFanLayout.setOnClickListener {
            updateIstFanUserCar()
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

        fun getUserCar(userId: String, callback:(UserCar?) -> Unit){
            db.collection("UserCar")
            .document(userId) // 사용자 ID로 문서 가져오기
            .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userCar = document.toObject(UserCar::class.java)
                        callback(userCar) // 변환한 User 객체를 callback으로 전달
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

    fun updateIsLockUserCar(isLock: Boolean) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Firestore에서 UserCar 문서 가져오기
            db.collection("UserCar")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // isOpen 값을 업데이트
                        db.collection("UserCar")
                            .document(userId)
                            .update("isLock", isLock)
                            .addOnSuccessListener {
                                // UI 업데이트
                                if (isLock) {
                                    statusLockTextView.text = "열림"
                                } else {
                                    statusLockTextView.text = "닫힘"
                                }
                            }
                            .addOnFailureListener { e ->
                                println("Error updating document: $e")
                            }
                    } else {
                        println("Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting document: $exception")
                }
        }
    }

    fun updateIstFanUserCar() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Firestore에서 UserCar 문서 가져오기
            db.collection("UserCar")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val currentFanStatus = document.getBoolean("isFan") ?: false // 현재 isFan 상태 가져오기

                        // isFan 값을 반전시켜 업데이트
                        db.collection("UserCar")
                            .document(userId)
                            .update("isFan", !currentFanStatus)
                            .addOnSuccessListener {
                                // UI 업데이트
                                if (!currentFanStatus) {
                                    statusFanTextView.text = "켜짐"
                                } else {
                                    statusFanTextView.text = "꺼짐"
                                }
                            }
                            .addOnFailureListener { e ->
                                println("Error updating document: $e")
                            }
                    } else {
                        println("Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting document: $exception")
                }
        }
    }
    private fun startAutoScroll(scrollView: HorizontalScrollView, cards: List<ImageView>) {
        val scrollRunnable = object : Runnable {
            override fun run() {
                // 다음 카드로 이동
                currentCardIndex = (currentCardIndex + 1) % cards.size

                // ScrollView로 스크롤 이동 (좌우 스크롤)
                val targetCard = cards[currentCardIndex]
                scrollView.smoothScrollTo(targetCard.left, 0)

                // 카드 강조 애니메이션
                highlightCard(cards)

                // 3초 후 반복 실행
                handler.postDelayed(this, 3000)
            }
        }

        handler.post(scrollRunnable)
    }

    private fun highlightCard(cards: List<ImageView>) {
        cards.forEachIndexed { index, card ->
            if (index == currentCardIndex) {
                // 확대 애니메이션
                card.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start()
            } else {
                // 원래 크기로 축소
                card.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Handler 작업 중단
        handler.removeCallbacksAndMessages(null)
    }

}