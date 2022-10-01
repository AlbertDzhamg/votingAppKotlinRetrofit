package ru.students.services

import org.ktorm.dsl.eq
import org.ktorm.entity.*
import ru.students.models.Question
import ru.students.models.QuestionRequest
import ru.students.models.Questions

class QuestionService {
    private val connection = DatabaseService.getConnection();

    fun create(request: QuestionRequest): Boolean = connection.sequenceOf(Questions)
        .add(Question {
            Vote = VoteService().getById(request.Vote)!!
            Content = request.Content
            DateVote = request.DateVote
        }) == 1

    fun list(): Set<Question> =
        connection.sequenceOf(Questions).toSet()

    fun getById(id: Int): Question? {
        println(id)
        val a =connection.sequenceOf(Questions)
            .find { question -> question.Id eq id }
        println(a)
        return a
    }


    fun getByVoteId(id: Int): Set<Question>? =
        connection.sequenceOf(Questions)
            .filter { question -> question.Vote eq id }.toSet()


    fun update(id: Int, request: QuestionRequest): Boolean {
        val question = getById(id)
        question?.Vote = VoteService().getById(request.Vote)!!
        question?.Content = request.Content
        question?.DateVote = request.DateVote
        return question?.flushChanges() == 1
    }

    fun delete(id: Int): Boolean = getById(id)?.delete() == 1
}