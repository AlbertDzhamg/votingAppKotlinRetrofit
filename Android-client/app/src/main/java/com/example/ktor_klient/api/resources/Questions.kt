package com.example.ktor_klient.api.resources

import io.ktor.resources.*
import kotlinx.serialization.*

@Serializable
@Resource("/chair")
class Questions() {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Questions = Questions(), val id: Int)
    @Serializable
    @Resource("vote")
    class Vote(val parent: Questions = Questions()){
        @Serializable
        @Resource("{id}")
        class Id(val parent: Vote = Vote(), val id: Int)
    }

}