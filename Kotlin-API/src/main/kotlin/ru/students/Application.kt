package ru.students

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.students.plugins.configureSerialization
import ru.students.routes.configureChoiceRoutes
import ru.students.routes.configureQuestionRoutes
import ru.students.routes.configureUserRoutes
import ru.students.routes.configureVoteRoutes

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        configureQuestionRoutes()
        configureChoiceRoutes()
        configureUserRoutes()
        configureVoteRoutes()
        configureSerialization()
    }.start(wait = true)
}
