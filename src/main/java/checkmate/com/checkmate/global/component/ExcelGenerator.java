
package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelGenerator {
    private final WorkbookToMultipartFileConverter workbookToMultipartFileConverter;

    public ExcelGenerator(WorkbookToMultipartFileConverter workbookToMultipartFileConverter) {
        this.workbookToMultipartFileConverter = workbookToMultipartFileConverter;
    }

    public MultipartFile generateExcel(String eventName, List<EventSchedule> eventSchedules, int completion) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet(eventName + " 참석자 명단");

            Row headerRow = sheet.createRow(0);
            String[] baseColumns = {"이름", "학과", "학번"};
            for (int j = 0; j < baseColumns.length; j++) {
                Cell cell = headerRow.createCell(j);
                cell.setCellValue(baseColumns[j]);
            }
            for (int i = 0; i < eventSchedules.size(); i++) {
                Cell cell = headerRow.createCell(baseColumns.length + i);
                cell.setCellValue((i + 1) + "회차");
            }
            Cell totalAttendanceCell = headerRow.createCell(baseColumns.length + eventSchedules.size());
            totalAttendanceCell.setCellValue("이수여부");

            Map<String, String[]> attendanceMap = new HashMap<>();

            for (int i = 0; i < eventSchedules.size(); i++) {
                EventSchedule eventSchedule = eventSchedules.get(i);
                List<EventAttendanceList> attendanceLists = eventSchedule.getEventAttendanceLists();
                for (EventAttendanceList attendance : attendanceLists) {
                    String key = attendance.getName() + "_" + attendance.getStudentNumber();
                    if (!attendanceMap.containsKey(key)) {
                        String[] info = new String[baseColumns.length + eventSchedules.size() + 1];
                        info[0] = attendance.getName();
                        info[1] = attendance.getMajor();
                        info[2] = String.valueOf(attendance.getStudentNumber());
                        attendanceMap.put(key, info);
                    }
                    String[] info = attendanceMap.get(key);
                    info[baseColumns.length + i] = attendance.isAttendance() ? "O" : "X";
                }
            }

            int rowNum = 1;
            for (String[] info : attendanceMap.values()) {
                int attendanceCount = 0;
                for (int j = baseColumns.length; j < baseColumns.length + eventSchedules.size(); j++) {
                    if ("O".equals(info[j])) {
                        attendanceCount++;
                    }
                }
                info[baseColumns.length + eventSchedules.size()] = attendanceCount >= completion ? "이수" : "미이수";

                Row row = sheet.createRow(rowNum++);
                for (int j = 0; j < info.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(info[j]);
                }
            }

            String fileName = eventName + "_참석자명단_전체.xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
                MultipartFile attendanceListMultipartFile = workbookToMultipartFileConverter.convert(workbook, eventName + "_참석자명단.xlsx");
                return attendanceListMultipartFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
