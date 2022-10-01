package com.example.ktor_klient.api.resources
import io.ktor.resources.*
import kotlinx.serialization.*

@Serializable
@Resource("/user")
class Users() {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: Int)

}