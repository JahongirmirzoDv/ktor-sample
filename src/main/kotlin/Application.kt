package uz.mobiledv

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import plugins.configureDatabase
import plugins.configureMonitoring
import plugins.configureRouting
import plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}