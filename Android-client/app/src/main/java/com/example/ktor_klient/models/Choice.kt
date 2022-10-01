package com.example.ktor_klient.models

import kotlinx.serialization.Serializable

@Serializable
data class Choice(
    var Id: Int,
    var Question: Int,
    var User: Int,
    var ChoiceUser: String
)
@Serializable
data class ChoiceRequest(
    var Question: Int,
    var User: Int,
    var ChoiceUser: String
)