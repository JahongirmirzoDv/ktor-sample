package services

import models.TimeTable
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ExcelService {
    
    fun parseExcelFile(inputStream: InputStream): List<TimeTable> {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val timeTables = mutableListOf<TimeTable>()
        
        // Skip header row, start from index 1
        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            
            // Check if row is empty
            if (row.getCell(0) == null || row.getCell(0).stringCellValue.isBlank()) {
                continue
            }
            
            val timeTable = TimeTable(
                group = getCellValueAsString(row.getCell(0)),
                day = getCellValueAsString(row.getCell(1)),
                time = getCellValueAsString(row.getCell(2)), // Ensure numeric time is converted
                className = getCellValueAsString(row.getCell(3)),
                teacherName = getCellValueAsString(row.getCell(4)),
                room = getCellValueAsString(row.getCell(5)) // Ensure numeric room is converted
            )
            
            // Validate that required fields are not empty
            if (timeTable.group.isNotBlank() && 
                timeTable.day.isNotBlank() && 
                timeTable.time.isNotBlank() && 
                timeTable.className.isNotBlank()) {
                timeTables.add(timeTable)
            }
        }
        
        workbook.close()
        return timeTables
    }
    
    private fun getCellValueAsString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""

        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cell.localDateTimeCellValue.toString() // Convert Date to String
                } else {
                    cell.numericCellValue.toInt().toString() // Convert Number to String
                }
            }
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> ""
        }
    }
}