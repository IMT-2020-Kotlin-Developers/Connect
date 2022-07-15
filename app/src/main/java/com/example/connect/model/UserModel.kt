package com.example.connect.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel (

    var uid : String? = null,
    var fullName : String? = null,
    var bio :String? = null,
    var photoURL : String? = null,
    var following: ArrayList<String>? = null,
    var followers: ArrayList<String>? = null
        ): Parcelable
