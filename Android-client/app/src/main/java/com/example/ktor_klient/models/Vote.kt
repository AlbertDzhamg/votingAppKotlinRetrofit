package com.example.ktor_klient.models

import kotlinx.serialization.Serializable

@Serializable
data class Vote(
    var Id: Int,
    var Title: String,
    var DateStart: String,
    var DateFinish: String,
    var Status: String
)

@Serializable
data class VoteRequest(
    var Title: String,
    var DateStart: String,
    var DateFinish: String,
    var Status: String
)