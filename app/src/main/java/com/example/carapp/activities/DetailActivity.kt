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
    private lateinit var purchaseButton: Button
    private var carId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayShowTitleEnabled(false) // 제목 숨김

        toolbar.setNavigationOnClickListener {
            onBackPressed() // 뒤로가기 버튼 동작
        }

        // 뷰 초기화
        carName = findViewById(R.id.carName)
        carPrice = findViewById(R.id.carPrice)
        carFuelEfficiency = findViewById(R.id.carFuelEfficiency)
        carDisplacement = findViewById(R.id.carDisplacement)
        carType = findViewById(R.id.carType)
        carImage = findViewById(R.id.carImage)
        purchaseButton = findViewById(R.id.purchaseButton)

        // 전달받은 자동차 정보 표시
        val car = intent.getParcelableExtra<Car>("car")
        car?.let {
            carId = it.id
            displayCarDetails(it)
        }

        // 구매 버튼 클릭 시 처리
        purchaseButton.setOnClickListener {
            Toast.makeText(this, "구매를 진행합니다.", Toast.LENGTH_SHORT).show()
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

        // Glide로 이미지 리소스 로드
        Glide.with(this)
            .load(car.imageResId)
            .into(carImage)
    }
}
