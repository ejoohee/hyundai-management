package com.example.carapp.api

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double // 현재 온도
)

data class Weather(
    val description: String // 날씨 설명
)