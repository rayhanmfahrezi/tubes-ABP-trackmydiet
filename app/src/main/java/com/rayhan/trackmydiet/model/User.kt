package com.rayhan.trackmydiet.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class User(
    @get:Exclude var id: String = "",
    var email : String ="",
) : Serializable

data class UserData(
    var age: String = "",
    var gender: String = "",
    var height: String = "",
    var weight: String = ""
) : Serializable
