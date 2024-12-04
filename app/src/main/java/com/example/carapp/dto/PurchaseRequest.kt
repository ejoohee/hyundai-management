package com.example.carapp.dto

data class PurchaseRequest(
    val carId: String,    // 구매할 차량 ID
    val userId: String,   // 사용자 ID
    val quantity: Int     // 구매 수량
)
