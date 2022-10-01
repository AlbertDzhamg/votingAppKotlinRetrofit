package ru.students.models

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.*

@Serializable
data class UserRequest(
    var FirstName: String,
    var LastName: String,
    var Email: String,
    var Phone: String,
    var Status: Boolean
)

@Serializable
data class UserResponse(
    var Id: Int?,
    var FirstName: String,
    var LastName: String,
    var Email: String,
    var Phone: String,
    var Status: Boolean
)

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    var Id: Int?
    var FirstName: String
    var LastName: String
    var Email: String
    var Phone: String
    var Status: Boolean
}

object Users : Table<User>("User") {
    var Id = int("Id").primaryKey().bindTo(User::Id)
    var FirstName = varchar("FirstName").bindTo(User::FirstName)
    var LastName = varchar("LastName").bindTo(User::LastName)
    var Email = varchar("Email").bindTo(User::Email)
    var Phone = varchar("Phone").bindTo(User::Phone)
    var Status = boolean("Status").bindTo(User::Status)
}