package com.example.carapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private lateinit var imageViewFanOff: ImageView
    private lateinit var imageViewFanOn: ImageView

    private lateinit var imageButtonMinus: ImageButton
    private lateinit var imageButtonPlus: ImageButton
    private lateinit var textViewTemperature: TextView
    private lateinit var seekBar: SeekBar
    private var currentTemperature: Float = 24.0f

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
        imageViewFanOff = view.findViewById(R.id.imageViewFanOff)
        imageViewFanOn = view.findViewById(R.id.imageViewFanOn)

        imageButtonMinus = view.findViewById(R.id.imageButtonMinus)
        imageButtonPlus = view.findViewById(R.id.imageButtonPlus)
        textViewTemperature = view.findViewById(R.id.textViewTemperature)
        seekBar = view.findViewById(R.id.seekBar)

        // SeekBar 초기화
        seekBar.max = 20 // 온도 범위 16.0℃ ~ 36.0℃에 맞춤
        updateTemperatureUI()
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
            setBackground(imageViewLocked, it.isLock)
            setBackground(imageViewOpen, !it.isLock)
            setBackground(imageViewPowerOn, it.isPower)
            setBackground(imageViewPowerOff, !it.isPower)
            setBackground(imageViewFanOn, it.isFan)
            setBackground(imageViewFanOff, !it.isFan)
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
        imageViewFanOn.setOnClickListener { handleFanToggle() }
        imageViewFanOff.setOnClickListener { handleFanToggle() }

        imageButtonMinus.setOnClickListener {
            adjustTemperature(-0.5f)
        }

        imageButtonPlus.setOnClickListener {
            adjustTemperature(0.5f)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTemperature = 16.0f + progress * 0.5f
                    updateTemperatureUI()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun adjustTemperature(delta: Float) {
        val newTemperature = currentTemperature + delta
        if (newTemperature in 16.0f..36.0f) {
            currentTemperature = newTemperature
            updateTemperatureUI()
        }
    }

    private fun updateTemperatureUI() {
        textViewTemperature.text = String.format("%.1f℃", currentTemperature)
        seekBar.progress = ((currentTemperature - 16.0f) / 0.5f).toInt()
    }


    private fun handleUserCarUpdate(field: String, value: Boolean) {
        userCar?.let {
            when (field) {
                "isLock" -> it.isLock = value
                "isPower" -> {
                    it.isPower = value
                    if (!value) { // 시동이 꺼지면 선풍기 끄기
                        it.isFan = false
                    }
                }
                "isFan" -> it.isFan = value
            }
            setUI() // 클릭 후 즉시 UI 업데이트
            updateFirestore(it)
        }
    }

    private fun handleFanToggle() {
        userCar?.let {
            if (it.isPower) { // 시동이 켜져 있는 경우에만 선풍기 켜기
                val isFanOn = !it.isFan
                it.isFan = isFanOn
                setUI() // UI 업데이트
                updateFirestore(it)

                // Toast 메시지 표시
                val fanStatus = if (isFanOn) "선풍기가 켜졌습니다" else "선풍기가 꺼졌습니다"
                Toast.makeText(context, fanStatus, Toast.LENGTH_SHORT).show()
            } else {
                // 시동이 꺼져 있으면 선풍기를 꺼진 상태로 설정
                it.isFan = false
                setUI()
                updateFirestore(it)

                // Toast 메시지 표시
                Toast.makeText(context, "시동과 함께 선풍기가 꺼졌습니다", Toast.LENGTH_SHORT).show()
            }
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
