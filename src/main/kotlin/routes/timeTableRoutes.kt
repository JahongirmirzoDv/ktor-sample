package routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.TimeTable
import repositories.TimeTableRepository
import services.ExcelService

fun Route.timeTableRoutes(
    timeTableRepository: TimeTableRepository,
    excelService: ExcelService
) {
    get("/") {
        call.respondText("Assalomu Alaykum!")
    }

    // Get all timetables
    get("/api/timetables") {
        val timeTables = timeTableRepository.getAllTimeTables()
        call.respond(timeTables)
    }

    // Get timetable by ID
    get("/api/timetables/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
            return@get
        }

        val timeTable = timeTableRepository.getTimeTableById(id)
        if (timeTable == null) {
            call.respond(HttpStatusCode.NotFound, "TimeTable not found")
        } else {
            call.respond(timeTable)
        }
    }

    // Get timetables by group
    get("/api/timetables/group/{group}") {
        val group = call.parameters["group"] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            "Group parameter is required"
        )
        val timeTables = timeTableRepository.getTimeTablesByGroup(group)
        call.respond(timeTables)
    }

    // Create a new timetable
    post("/api/timetables") {
        val timeTable = call.receive<TimeTable>()
        val id = timeTableRepository.insertTimeTable(timeTable)
        call.respond(HttpStatusCode.Created, mapOf("id" to id))
    }

    // Update a timetable
    put("/api/timetables/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
            return@put
        }

        val timeTable = call.receive<TimeTable>()
        val updated = timeTableRepository.updateTimeTable(id, timeTable)
        if (updated) {
            call.respond(HttpStatusCode.OK, "TimeTable updated successfully")
        } else {
            call.respond(HttpStatusCode.NotFound, "TimeTable not found")
        }
    }

    // Delete a timetable
    delete("/api/timetables/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
            return@delete
        }

        val deleted = timeTableRepository.deleteTimeTable(id)
        if (deleted) {
            call.respond(HttpStatusCode.OK, "TimeTable deleted successfully")
        } else {
            call.respond(HttpStatusCode.NotFound, "TimeTable not found")
        }
    }

    // Delete all timetables
    delete("/api/timetables") {
        val deleted = timeTableRepository.deleteAllTimeTables()
        if (deleted) {
            call.respond(HttpStatusCode.OK, "All TimeTables deleted successfully")
        } else {
            call.respond(HttpStatusCode.NotFound, "No TimeTables found to delete")
        }
    }

    // Upload Excel file
    post("/api/upload") {
        val multipartData = call.receiveMultipart()
        var success = false // Track success to prevent catch execution

        try {
            multipartData.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName ?: "upload.xlsx"
                    val fileBytes = part.streamProvider().readBytes()

                    val timeTables = excelService.parseExcelFile(fileBytes.inputStream())
                    if (timeTables.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "No valid data found in the Excel file")
                        return@forEachPart
                    }

                    val insertedIds = timeTableRepository.insertTimeTables(timeTables)
                    success = true // Mark as success
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf(
                            "message" to "File uploaded and processed successfully",
                            "recordsInserted" to timeTables.size,
                            "ids" to insertedIds
                        )
                    )
                }
                part.dispose()
            }
        } catch (e: Exception) {
            if (!success) { // Only respond with an error if we haven't already succeeded
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Error processing Excel file: ${e.message}"
                )
            }
        }
    }

    get("/warmup") {
        call.respondText("OK", status = HttpStatusCode.OK)
    }

    get("/health") {
        call.respondText("OK", status = HttpStatusCode.OK)
    }
}