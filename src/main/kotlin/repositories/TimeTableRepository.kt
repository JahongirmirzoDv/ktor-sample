package repositories

import models.TimeTable
import models.TimeTables
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TimeTableRepository {

    fun getAllTimeTables(): List<TimeTable> = transaction {
        TimeTables.selectAll().map { it.toTimeTable() }
    }

    fun getTimeTableById(id: Int): TimeTable? = transaction {
        TimeTables.select { TimeTables.id eq id }
            .mapNotNull { it.toTimeTable() }
            .singleOrNull()
    }

    fun getTimeTablesByGroup(group: String): List<TimeTable> = transaction {
        TimeTables.select { TimeTables.group eq group }
            .map { it.toTimeTable() }
    }

    fun insertTimeTable(timeTable: TimeTable): Int = transaction {
        TimeTables.insert {
            it[group] = timeTable.group
            it[day] = timeTable.day
            it[time] = timeTable.time
            it[className] = timeTable.className
            it[teacherName] = timeTable.teacherName
            it[room] = timeTable.room
        }[TimeTables.id]
    }

    fun insertTimeTables(timeTables: List<TimeTable>): List<Int> = transaction {
        timeTables.map { timeTable ->
            TimeTables.insert {
                it[group] = timeTable.group
                it[day] = timeTable.day
                it[time] = timeTable.time
                it[className] = timeTable.className
                it[teacherName] = timeTable.teacherName
                it[room] = timeTable.room
            }[TimeTables.id]
        }
    }

    fun updateTimeTable(id: Int, timeTable: TimeTable): Boolean = transaction {
        TimeTables.update({ TimeTables.id eq id }) {
            it[group] = timeTable.group
            it[day] = timeTable.day
            it[time] = timeTable.time
            it[className] = timeTable.className
            it[teacherName] = timeTable.teacherName
            it[room] = timeTable.room
        } > 0
    }

    fun deleteTimeTable(id: Int): Boolean = transaction {
        TimeTables.deleteWhere { TimeTables.id eq id } > 0
    }

    private fun ResultRow.toTimeTable(): TimeTable {
        return TimeTable(
            id = this[TimeTables.id],
            group = this[TimeTables.group],
            day = this[TimeTables.day],
            time = this[TimeTables.time],
            className = this[TimeTables.className],
            teacherName = this[TimeTables.teacherName],
            room = this[TimeTables.room]
        )
    }

    fun deleteAllTimeTables(): Boolean {
        return transaction {
            TimeTables.deleteAll() > 0
        }
    }
}