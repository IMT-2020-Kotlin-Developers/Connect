package com.example.connect.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LikeModel(
    var likeNumber : Int = 0,
    var uid : String = "",
) : Parcelable
