package com.example.ktor_klient.api.resources
import io.ktor.resources.*
import kotlinx.serialization.*

@Serializable
@Resource("/vote")
class Votes() {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Votes = Votes(), val id: Int)

}