package ru.students.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.students.models.*
import ru.students.services.QuestionService

private fun Question?.toResponse(): QuestionResponse? =
    this?.let {
        QuestionResponse(
            it.Id!!,
            it.Vote.Id!!,
            it.Content,
            it.DateVote
        )
    }

fun Application.configureQuestionRoutes() {
    routing {
        route("/question") {
            val questionService = QuestionService()
            createRoute(questionService)
            listRoute(questionService)
            getByIdRoute(questionService)
            getByVoteIdRoute(questionService)
            updateRoute(questionService)
            deleteRoute(questionService)
        }
    }
}

fun Route.createRoute(service: QuestionService) {
    post {
        if (service.create(call.receive<QuestionRequest>()))
            call.respond(HttpStatusCode.Created)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка при создании результата голосования"))
    }
}

fun Route.listRoute(service: QuestionService) {
    get {
        call.respond(
            message = service.list()
                .map(Question::toResponse)
        )
    }
}

fun Route.getByIdRoute(service: QuestionService) {
    get("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        service.getById(id)
            ?.let { question -> question.toResponse() }
            ?.let { response -> call.respond(response) }
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Вопрос голосования с Id:[$id] не найден"))
    }
}

fun Route.getByVoteIdRoute(service: QuestionService) {
    get("/vote/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        service.getByVoteId(id)
            ?.let { question -> question.map(Question::toResponse) }
            ?.let { response -> call.respond(response) }
            ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Вопрос голосования с Id голосованием :[$id] не найдена"))
    }
}

fun Route.updateRoute(service: QuestionService) {
    patch("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        if (service.update(id, call.receive<QuestionRequest>()))
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка при обновлении вопроса голосования с Id:[$id]"))
    }
}

fun Route.deleteRoute(service: QuestionService) {
    delete("/{id}") {
        val id: Int = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Неправильный Id"))

        if (service.delete(id))
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невозможно удалить вопрос голосования с таким Id:[$id]"))
    }
}