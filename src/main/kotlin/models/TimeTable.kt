package models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class TimeTable(
    val id: Int? = null,
    val group: String,
    val day: String,
    val time: String,
    val className: String,
    val teacherName: String,
    val room: String
)

object TimeTables : Table() {
    val id = integer("id").autoIncrement()
    val group = varchar("group_name", 50)
    val day = varchar("day", 20)
    val time = varchar("time", 20)
    val className = varchar("class_name", 100)
    val teacherName = varchar("teacher_name", 100)
    val room = varchar("room", 50)

    override val primaryKey = PrimaryKey(id)
}