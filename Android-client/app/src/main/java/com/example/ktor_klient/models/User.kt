package com.example.ktor_klient.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var Id: Int,
    var FirstName: String,
    var LastName: String,
    var Email: String,
    var Phone: String,
    var Status: Boolean
)

@Serializable
data class UserRequest(
    var FirstName: String,
    var LastName: String,
    var Email: String,
    var Phone: String,
    var Status: Boolean
)
