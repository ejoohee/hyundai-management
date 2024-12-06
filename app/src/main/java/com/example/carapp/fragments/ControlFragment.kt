package com.example.carapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.carapp.R
import com.example.carapp.models.UserCar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ControlFragment : Fragment() {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private var userCar: UserCar? = null
    private var userId: String? = null

    private lateinit var imageViewLocked: ImageView
    private lateinit var imageViewOpen: ImageView
    private lateinit var imageViewPowerOff: ImageView
    private lateinit var imageViewPowerOn: ImageView
    private lateinit var imageViewSignOff: ImageView
    private lateinit var imageViewSignOn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_control, container, false)
        initializeViews(view)
        fetchUserCarData()
        setOnClickListeners()
        return view
    }

    private fun initializeViews(view: View) {
        imageViewLocked = view.findViewById(R.id.imageViewLocked)
        imageViewOpen = view.findViewById(R.id.imageViewOpen)
        imageViewPowerOff = view.findViewById(R.id.imageViewPowerOff)
        imageViewPowerOn = view.findViewById(R.id.imageViewPowerOn)
        imageViewSignOff = view.findViewById(R.id.imageViewFanOff)
        imageViewSignOn = view.findViewById(R.id.imageViewFanOn)
    }

    private fun fetchUserCarData() {
        userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            firestore.collection("UserCar")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        userCar = documents.first().toObject(UserCar::class.java)
                        setUI()
                    } else {
                        Log.w("ControlFragment", "No UserCar found for userId: $uid")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ControlFragment", "Error fetching user car data", e)
                }
        } ?: Log.e("ControlFragment", "User ID is null")
    }

    private fun setUI() {
        userCar?.let {
            // 문 잠금 상태
            setBackground(imageViewLocked, it.isLock)
            setBackground(imageViewOpen, !it.isLock)
            // 시동 상태
            setBackground(imageViewPowerOn, it.isPower)
            setBackground(imageViewPowerOff, !it.isPower)
            // 선풍기 상태
            setBackground(imageViewSignOn, it.isFan)
            setBackground(imageViewSignOff, !it.isFan)
        }
    }

    private fun setBackground(view: ImageView, isActive: Boolean) {
        view.setBackgroundResource(
            if (isActive) R.drawable.circle_background_on else R.drawable.circle_background
        )
    }

    private fun setOnClickListeners() {
        imageViewLocked.setOnClickListener { handleUserCarUpdate("isLock", true) }
        imageViewOpen.setOnClickListener { handleUserCarUpdate("isLock", false) }
        imageViewPowerOn.setOnClickListener { handleUserCarUpdate("isPower", true) }
        imageViewPowerOff.setOnClickListener { handleUserCarUpdate("isPower", false) }
        imageViewSignOn.setOnClickListener { handleUserCarUpdate("isFan", true) }
        imageViewSignOff.setOnClickListener { handleUserCarUpdate("isFan", false) }
    }

    private fun handleUserCarUpdate(field: String, value: Boolean) {
        userCar?.let {
            when (field) {
                "isLock" -> it.isLock = value
                "isPower" -> it.isPower = value
                "isFan" -> it.isFan = value
            }
            setUI() // 클릭 후 즉시 UI 업데이트
            updateFirestore(it)
        }
    }

    private fun updateFirestore(updatedCar: UserCar) {
        firestore.collection("UserCar")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentId = querySnapshot.first().id
                    firestore.collection("UserCar").document(documentId)
                        .set(updatedCar)
                        .addOnSuccessListener {
                            Log.d("ControlFragment", "UserCar updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ControlFragment", "Error updating UserCar", e)
                        }
                } else {
                    Log.e("ControlFragment", "No documents found to update")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ControlFragment", "Error fetching documents for update", e)
            }
    }
}
