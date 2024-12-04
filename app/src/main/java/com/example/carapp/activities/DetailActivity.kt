package com.example.carapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.carapp.R
import com.example.carapp.models.Car

class DetailActivity : AppCompatActivity() {
    private lateinit var carName: TextView
    private lateinit var carPrice: TextView
    private lateinit var carFuelEfficiency: TextView
    private lateinit var carDisplacement: TextView
    private lateinit var carType: TextView
    private lateinit var carImage: ImageView
    private lateinit var registerButton: Button
    private var carId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // 뷰 초기화
        carName = findViewById(R.id.carName)
        carPrice = findViewById(R.id.carPrice)
        carFuelEfficiency = findViewById(R.id.carFuelEfficiency)
        carDisplacement = findViewById(R.id.carDisplacement)
        carType = findViewById(R.id.carType)
        carImage = findViewById(R.id.carImage)
        registerButton = findViewById(R.id.purchaseButton)

        val car = intent.getParcelableExtra<Car>("car")
        car?.let {
            carId = it.id
            displayCarDetails(it)
        }

        registerButton.setOnClickListener {
            Toast.makeText(this, "차량 등록 완료했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayCarDetails(car: Car) {
        carName.text = car.name
        carPrice.text = "${car.price} 만원"
        carFuelEfficiency.text = if (car.fuelEfficiency > 0) {
            "연비: ${car.fuelEfficiency} km/L"
        } else {
            "연비 정보 없음"
        }
        carDisplacement.text = if (car.displacement > 0) {
            "배기량: ${car.displacement} cc"
        } else {
            "배기량 정보 없음"
        }
        carType.text = "차종: ${car.type}"

        Glide.with(this)
            .load(car.imageResId)
            .into(carImage)
    }
}
