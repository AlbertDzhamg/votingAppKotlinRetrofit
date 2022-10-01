package ru.students.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.students.models.*
import ru.students.services.VoteService

private fun Vote?.toResponse(): VoteResponse? =
    this?.let {
        VoteResponse(
            it.Id!!,
            it.Title,
            it.DateStart,
            it.DateFinish,
            it.Status
        )
    }

fun Application.configureVoteRoutes() {
    routing {
        route("/vote") {
            val voteService = VoteService()
            createRoute(voteService)
            listRoute(voteService)
            getByIdRoute(voteService)
            updateRoute(voteService)
            deleteRoute(voteService)
        }
    }
}

fun Route.createRoute(service: VoteService) {
    post {
        if (service.create(call.receive<VoteRequest>()))
            call.respond(HttpStatusCode.Created)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка при создании голосования"))
    }
}

fun Route.listRoute(service: VoteService) {
    get {
        call.respond(
            message = service.list()
                .map(Vote::toResponse)
        )
    }
}

fun Route.getByIdRoute(service: VoteService) {
    get("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        service.getById(id)
            ?.let { vote -> vote.toResponse() }
            ?.let { response -> call.respond(response) }
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Голосование с Id:[$id] не найден"))
    }
}

fun Route.updateRoute(service: VoteService) {
    patch("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        if (service.update(id, call.receive<VoteRequest>()))
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка при обновлении голосования с Id:[$id]"))
    }
}

fun Route.deleteRoute(service: VoteService) {
    delete("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        if (service.delete(id))
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невозможно удалить голосование с таким Id:[$id]"))
    }
}