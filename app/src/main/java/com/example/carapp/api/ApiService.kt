package com.example.carapp.api

import com.example.carapp.dto.PurchaseRequest
import com.example.carapp.dto.PurchaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// 구매 요청 데이터 모델
interface ApiService {
    @POST("purchase") // MockAPI 경로 예시
    suspend fun purchaseCar(@Body purchaseRequest: PurchaseRequest): Response<PurchaseResponse>
}
