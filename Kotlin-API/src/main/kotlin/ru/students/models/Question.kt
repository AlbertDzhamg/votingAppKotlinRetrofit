package ru.students.models

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.*

@Serializable
data class QuestionRequest(
    var Vote: Int,
    var Content: String,
    var DateVote: String,
)

@Serializable
data class QuestionResponse(
    var Id: Int?,
    var Vote: Int,
    var Content: String,
    var DateVote: String
)

interface Question : Entity<Question> {
    companion object : Entity.Factory<Question>()

    var Id: Int?
    var Vote: Vote
    var Content: String
    var DateVote: String
}

object Questions : Table<Question>("Question") {
    var Id = int("Id").primaryKey().bindTo(Question::Id)
    var Vote = int("Vote").references(Votes) { it.Vote }
    var Content = varchar("Content").bindTo(Question::Content)
    var DateVote = varchar("DateVote").bindTo(Question::DateVote)
}