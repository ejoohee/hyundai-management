package com.example.carapp.activities

import android.content.Intent
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
    private lateinit var submitButton: Button
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
        carPrice = findViewById(R.id.carPriceTextView)
        carFuelEfficiency = findViewById(R.id.carFuelEfficiencyTextView)
        carDisplacement = findViewById(R.id.carDisplacementTextView)
        carType = findViewById(R.id.carTypeTextView)
        carImage = findViewById(R.id.carImage)
        submitButton = findViewById(R.id.submitButton)

        val car = intent.getParcelableExtra<Car>("car")
        car?.let {
            carId = it.id
            displayCarDetails(it)
        }

        submitButton.setOnClickListener {
            if (car != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("car", car) // 선택된 자동차 정보를 전달
                setResult(RESULT_OK, resultIntent)
                finish() // DetailActivity 종료 후 CarListActivity로 돌아감
            } else {
                Toast.makeText(this, "자동차 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayCarDetails(car: Car) {
        carName.text = car.name
        carPrice.text = "${car.price} 만원"
        carFuelEfficiency.text = if (car.fuelEfficiency > 0) {
            "${car.fuelEfficiency} km/L"
        } else {
            "연비 정보 없음"
        }
        carDisplacement.text = if (car.displacement > 0) {
            "${car.displacement} cc"
        } else {
            "배기량 정보 없음"
        }
        carType.text = "${car.type}"

        Glide.with(this)
            .load(car.imageResId)
            .into(carImage)
    }



}
