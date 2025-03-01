package plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import repositories.TimeTableRepository
import routes.timeTableRoutes
import services.ExcelService

fun Application.configureRouting() {
    val timeTableRepository = TimeTableRepository()
    val excelService = ExcelService()
    
    routing {
        timeTableRoutes(timeTableRepository, excelService)
        
        // Static file serving for testing/debugging purposes
        static("/static") {
            resources("static")
        }
    }
}