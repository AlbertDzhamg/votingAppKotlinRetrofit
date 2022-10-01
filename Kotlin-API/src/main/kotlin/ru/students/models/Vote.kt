package ru.students.models

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.*

@Serializable
data class VoteRequest(
    var Title: String,
    var DateStart: String,
    var DateFinish: String,
    var Status: String
)

@Serializable
data class VoteResponse(
    var Id: Int?,
    var Title: String,
    var DateStart: String,
    var DateFinish: String,
    var Status: String
)

interface Vote : Entity<Vote> {
    companion object : Entity.Factory<Vote>()

    var Id: Int?
    var Title: String
    var DateStart: String
    var DateFinish: String
    var Status: String
}

object Votes : Table<Vote>("Vote") {
    var Id = int("Id").primaryKey().bindTo(Vote::Id)
    var Title = varchar("Title").bindTo(Vote::Title)
    var DateStart = varchar("DateStart").bindTo(Vote::DateStart)
    var DateFinish = varchar("DateFinish").bindTo(Vote::DateFinish)
    var Status = varchar("Status").bindTo(Vote::Status)
}