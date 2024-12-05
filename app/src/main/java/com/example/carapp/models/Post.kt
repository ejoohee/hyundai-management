package com.example.carapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post (
    var postId: String ="",
    var userId: String = "",
    var date: String = "",
    var title: String = "",
    var content: String ="",
    var image: String=""
): Parcelable