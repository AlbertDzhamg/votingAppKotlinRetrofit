package com.example.ktor_klient.api.resources

import io.ktor.resources.*
import kotlinx.serialization.*

@Serializable
@Resource("/choice")
class Choices() {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Choices = Choices(), val id: Int)
    @Serializable
    @Resource("question")
    class QuestionUser(val parent: Choices = Choices()){
        @Serializable
        @Resource("{quesId}/{usrId}")
        class Params(val parent: Questions.Vote = Questions.Vote(), val quesId: Int, val usrId: Int)
    }
}