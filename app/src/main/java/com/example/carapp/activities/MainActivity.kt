package com.example.carapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carapp.CarAdapter
import com.example.carapp.R
import com.example.carapp.models.Car

class MainActivity : AppCompatActivity() {

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

    private lateinit var carAdapter: CarAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()
        displayCars()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("car", car) // Car 객체를 전달
            startActivity(intent)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = carAdapter
        }
    }

    private fun displayCars() {
        carAdapter.submitList(carList)
    }
}