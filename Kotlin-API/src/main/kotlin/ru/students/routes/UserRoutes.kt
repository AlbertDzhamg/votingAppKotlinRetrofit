package ru.students.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.students.models.*
import ru.students.services.UserService

private fun User?.toResponse(): UserResponse? =
    this?.let {
        UserResponse(
            it.Id!!,
            it.FirstName,
            it.LastName,
            it.Email,
            it.Phone,
            it.Status
        )
    }

fun Application.configureUserRoutes() {
    routing {
        route("/user") {
            val userService = UserService()
            createRoute(userService)
            listRoute(userService)
            getByIdRoute(userService)
            updateRoute(userService)
            deleteRoute(userService)
        }
    }
}

fun Route.createRoute(service: UserService) {
    post {
        if (service.create(call.receive<UserRequest>()))
            call.respond(HttpStatusCode.Created)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка при создании пользователя"))
    }
}

fun Route.listRoute(service: UserService) {
    get {
        call.respond(
            message = service.list()
                .map(User::toResponse)
        )
    }
}

fun Route.getByIdRoute(service: UserService) {
    get("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        service.getById(id)
            ?.let { user -> user.toResponse() }
            ?.let { response -> call.respond(response) }
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Пользователь с Id:[$id] не найден"))
    }
}

fun Route.updateRoute(service: UserService) {
    patch("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        if (service.update(id, call.receive<UserRequest>()))
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка при обновлении пользователя с Id:[$id]"))
    }
}

fun Route.deleteRoute(service: UserService) {
    delete("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        if (service.delete(id))
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невозможно удалить пользователя с таким Id:[$id]"))
    }
}