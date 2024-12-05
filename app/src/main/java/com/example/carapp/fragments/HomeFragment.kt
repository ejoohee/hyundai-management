package com.example.carapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    // Firestore 인스턴스 초기화
    val db: FirebaseFirestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //내 차 정보
        val carTextView = view.findViewById<TextView>(R.id.carTextView)
        val carImageView = view.findViewById<ImageView>(R.id.carImageView)

        //power, connect, 주행거리 정보
        val isPowerTextView = view.findViewById<TextView>(R.id.isPowerTextView)
        val connectTextView = view.findViewById<TextView>(R.id.connectTextView)
        val distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
        
        //차량제어
        val lockTextView = view.findViewById<TextView>(R.id.lockTextView)
        val powerTextView = view.findViewById<TextView>(R.id.powerTextView)
        val openTextView = view.findViewById<TextView>(R.id.openTextView)

        //내 차량 상태
        val statusLockTextView = view.findViewById<TextView>(R.id.statusLockTextView)
        val statusFanTextView = view.findViewById<TextView>(R.id.statusFanTextView)
        val statusDoorTextView = view.findViewById<TextView>(R.id.statusDoorTextView)

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
}