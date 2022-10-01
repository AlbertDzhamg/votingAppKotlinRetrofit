package ru.students.services

import org.ktorm.dsl.eq
import org.ktorm.entity.*
import ru.students.models.Vote
import ru.students.models.VoteRequest
import ru.students.models.Votes

class VoteService {
    private val connection = DatabaseService.getConnection();

    fun create(request: VoteRequest): Boolean = connection.sequenceOf(Votes)
        .add(Vote {
            Title = request.Title
            DateStart = request.DateStart
            DateFinish = request.DateFinish
            Status = request.Status
        }) == 1

    fun list(): Set<Vote> =
        connection.sequenceOf(Votes).toSet()

    fun getById(id: Int): Vote? =
        connection.sequenceOf(Votes)
            .find { vote -> vote.Id eq id }

    fun update(id: Int, request: VoteRequest): Boolean {
        val vote = getById(id)
        vote?.Title = request.Title
        vote?.DateStart = request.DateStart
        vote?.DateFinish = request.DateFinish
        vote?.Status = request.Status
        return vote?.flushChanges() == 1
    }

    fun delete(id: Int): Boolean = getById(id)?.delete() == 1
}