package com.example.carapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.carapp.R
import com.example.carapp.activities.IntroActivity
import com.example.carapp.models.Car
import com.example.carapp.models.User
import com.example.carapp.models.UserCar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ControlFragment : Fragment() {
    val firestore = Firebase.firestore
    var userCar: UserCar? = null
    var userId: String? = null

    lateinit var imageViewLocked: ImageView
    lateinit var imageViewOpen: ImageView
    lateinit var imageViewPowerOff: ImageView
    lateinit var imageViewPowerOn: ImageView
    lateinit var imageViewSignOff: ImageView
    lateinit var imageViewSignOn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_control, container, false)

        imageViewLocked = view.findViewById(R.id.imageViewLocked)
        imageViewOpen = view.findViewById(R.id.imageViewOpen)
        imageViewPowerOff = view.findViewById(R.id.imageViewPowerOff)
        imageViewPowerOn = view.findViewById(R.id.imageViewPowerOn)
        imageViewSignOff = view.findViewById(R.id.imageViewSignOff)
        imageViewSignOn = view.findViewById(R.id.imageViewSignOn)

        // Todo: 유저카 가져오기: userID로 가져오기
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        userId = currentUser?.uid
        Log.d("ControlFragment", "userId: $userId")
        firestore.collection("UserCar")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                // 가져온 데이터를 리스트로 변환
//                val userCars = mutableListOf<UserCar>()
                for (document in documents) {
                    userCar = document.toObject(UserCar::class.java)
//                    userCars.add(userCar)
                }
                // 가져온 UserCar 리스트 출력 (디버깅용)
                /*userCars.forEach { car ->
                    Log.d("Firestore", "UserCar: $car")
                }*/
                setUI()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
            }


        // Todo: 유저카 정보 이용해 UI 상태 반영하기
        // Todo: 유저카 정보 변경하면 파이어스토어에 정보 저장하기


        setOnClickListner()


        return view
    }

    fun setUI() {
        if (userCar != null) {
            if (userCar!!.isLock) {
                imageViewLocked.setBackgroundResource(R.drawable.circle_background_on)
                imageViewOpen.setBackgroundResource(R.drawable.circle_background)
            } else {
                imageViewLocked.setBackgroundResource(R.drawable.circle_background)
                imageViewOpen.setBackgroundResource(R.drawable.circle_background_on)
            }
        }
    }

    fun setOnClickListner() {
        imageViewLocked.setOnClickListener {
            updateUserCarLock(true)
        }
        imageViewOpen.setOnClickListener {
            updateUserCarLock(false)
        }
    }

    fun updateUserCarLock(isLocked: Boolean) {
        userCar!!.isLock = isLocked

        firestore.collection("UserCar")
            .whereEqualTo("userId", userId) // userId로 필터링
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // 해당 문서에 객체 전체를 업데이트
                    firestore.collection("UserCar").document(document.id)
                        .set(userCar!!) // 기존 데이터 덮어씀
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document successfully updated with new object: ${document.id}")
                            setUI()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error updating document", e)
                        }
                }
                if (querySnapshot.isEmpty) {
                    Log.d("Firestore", "No documents found for userId: $userId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting documents", e)
            }
    }
}