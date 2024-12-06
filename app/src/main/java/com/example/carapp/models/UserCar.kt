package com.example.carapp.models

data class UserCar(
    var carId:String="",
    var userId:String="",
    //문 잠금
    var isLock:Boolean=false,
    //시동 키기
    var isPower:Boolean=false,
    //주행거리
    var distance:String="456q",
    //블루투스 연결
    var isConnect:Boolean=true,
    //에어컨 on off
    var isFan:Boolean=false,
    //실내온도
    var temperature:String="",
    //문 열림-닫힘
    var isOpen:Boolean=false
)