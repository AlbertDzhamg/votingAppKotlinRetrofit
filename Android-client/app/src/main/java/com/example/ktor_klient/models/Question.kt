package com.example.ktor_klient.models

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    var Id: Int,
    var Vote: Int,
    var Content: String,
    var DateVote: String
)
@Serializable
data class QuestionRequest(
    var Vote: Int,
    var Content: String,
    var DateVote: String
)
