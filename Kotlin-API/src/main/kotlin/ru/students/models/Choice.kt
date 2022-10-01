package ru.students.models

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.*

@Serializable
data class ChoiceRequest(
    var Question: Int,
    var User: Int,
    var ChoiceUser: String
)

@Serializable
data class ChoiceResponse(
    var Id: Int?,
    var Question: Int,
    var User: Int,
    var ChoiceUser: String
)

interface Choice : Entity<Choice> {
    companion object : Entity.Factory<Choice>()

    var Id: Int?
    var Question: Question
    var User: User
    var ChoiceUser: String
}

object Choices : Table<Choice>("Choice") {
    var Id = int("Id").primaryKey().bindTo(Choice::Id)
    var Question = int("Question").references(Questions) { it.Question }
    var User = int("User").references(Users) { it.User }
    var ChoiceUser = varchar("ChoiceUser").bindTo(Choice::ChoiceUser)
}