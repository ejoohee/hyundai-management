package com.example.carapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val fuelEfficiency: Double = 0.0, // 연비 (km/L)
    val displacement: Int = 0, // 배기량 (cc)
    val type: String = "",
//    val imageUrl: String = ""
    val imageResId: Int = 0
) : Parcelable
