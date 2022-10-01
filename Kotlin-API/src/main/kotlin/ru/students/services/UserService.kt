package ru.students.services

import org.ktorm.dsl.eq
import org.ktorm.entity.*
import ru.students.models.User
import ru.students.models.UserRequest
import ru.students.models.Users

class UserService {
    private val connection = DatabaseService.getConnection();

    fun create(request: UserRequest): Boolean = connection.sequenceOf(Users)
        .add(User {
            FirstName = request.FirstName
            LastName = request.LastName
            Email = request.Email
            Phone = request.Phone
            Status = request.Status
        }) == 1

    fun list(): Set<User> =
        connection.sequenceOf(Users).toSet()

    fun getById(id: Int): User? =
        connection.sequenceOf(Users)
            .find { user -> user.Id eq id }

    fun update(id: Int, request: UserRequest): Boolean {
        val user = getById(id)
        user?.FirstName = request.FirstName
        user?.LastName = request.LastName
        user?.Email = request.Email
        user?.Phone = request.Phone
        user?.Status = request.Status
        return user?.flushChanges() == 1
    }

    fun delete(id: Int): Boolean = getById(id)?.delete() == 1
}