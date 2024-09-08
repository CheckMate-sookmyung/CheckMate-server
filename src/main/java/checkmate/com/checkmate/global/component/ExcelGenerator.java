
package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.global.domain.EventTarget;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelGenerator {
    private final WorkbookToMultipartFileConverter workbookToMultipartFileConverter;
    private final EventAttendanceRepository eventAttendanceRepository;


    public MultipartFile generateExcel(Event event, List<EventSchedule> eventSchedules, int completion) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet(event.getEventTitle() + " 참석자 명단");

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
                List<EventAttendance> attendanceLists = eventAttendanceRepository.findEventAttendancesById(eventSchedule.getEventScheduleId());
                for (EventAttendance attendance : attendanceLists) {
                    if (event.getEventTarget() == EventTarget.INTERNAL) {
                        String key = attendance.getStudent().getStudentName() + "_" + attendance.getStudent().getStudentName();
                        if (!attendanceMap.containsKey(key)) {
                            String[] info = new String[baseColumns.length + eventSchedules.size() + 1];
                            info[0] = attendance.getStudent().getStudentName();
                            info[1] = attendance.getStudent().getStudentMajor();
                            info[2] = String.valueOf(attendance.getStudent().getStudentNumber());
                            attendanceMap.put(key, info);
                        }
                        String[] info = attendanceMap.get(key);
                        info[baseColumns.length + i] = attendance.isAttendance() ? "O" : "X";
                    } else {
                        String key = attendance.getStranger().getStrangerName() + "_" + attendance.getStranger().getStrangerPhoneNumber();
                        if (!attendanceMap.containsKey(key)) {
                            String[] info = new String[baseColumns.length + eventSchedules.size() + 1];
                            info[0] = attendance.getStranger().getStrangerName();
                            info[1] = attendance.getStranger().getStrangerAffiliation();
                            info[2] = String.valueOf(attendance.getStranger().getStrangerPhoneNumber());
                            attendanceMap.put(key, info);
                        }
                        String[] info = attendanceMap.get(key);
                        info[baseColumns.length + i] = attendance.isAttendance() ? "O" : "X";
                    }
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

            String fileName = event.getEventTitle() + "_참석자명단_전체.xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
                MultipartFile attendanceListMultipartFile = workbookToMultipartFileConverter.convert(workbook, event.getEventTitle() + "_참석자명단.xlsx");
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
/*

    public MultipartFile generateExcelAboutExternalEvent(String eventName, List<EventSchedule> eventSchedules, int completion) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet(eventName + " 참석자 명단");

            Row headerRow = sheet.createRow(0);
            String[] baseColumns = {"이름", "소속"};
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
                List<EventAttendance> attendanceLists = eventSchedule.getEventAttendances();
                for (EventAttendance attendance : attendanceLists) {
                    String key = attendance.getName();
                    if (!attendanceMap.containsKey(key)) {
                        String[] info = new String[baseColumns.length + eventSchedules.size() + 1];
                        info[0] = attendance.getName();
                        info[1] = attendance.getMajor();
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
                MultipartFile attendanceListMultipartFile = workbookToMultipartFileConverter.convert(workbook, fileName);
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
*/


}
