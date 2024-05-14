package checkmate.com.checkmate.eventattendanceList.service;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import com.amazonaws.util.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.List;

@Component
public class ExcelGenerator {

    private final WorkbookToMultipartFileConverter workbookToMultipartFileConverter;

    public ExcelGenerator(WorkbookToMultipartFileConverter workbookToMultipartFileConverter) {
        this.workbookToMultipartFileConverter = workbookToMultipartFileConverter;
    }

    public MultipartFile generateExcel(String eventName, List<EventSchedule> eventSchedules) {
        Workbook workbook = new XSSFWorkbook();
        try {
            for (int i = 0; i < eventSchedules.size(); i++) {
                EventSchedule eventSchedule = eventSchedules.get(i);
                String sheetName = (i + 1) + "회차(" + eventSchedule.getEventDate() + ")";
                Sheet sheet = workbook.createSheet(sheetName);

                Row headerRow = sheet.createRow(0);
                String[] columns = {"이름", "학과", "학번", "참석여부", "출석시간", "싸인"};
                for (int j = 0; j < columns.length; j++) {
                    Cell cell = headerRow.createCell(j);
                    cell.setCellValue(columns[j]);
                }

                List<EventAttendanceList> attendanceLists = eventSchedule.getEventAttendanceLists();
                int rowNum = 1;
                for (EventAttendanceList attendance : attendanceLists) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(attendance.getName());
                    row.createCell(1).setCellValue(attendance.getMajor());
                    row.createCell(2).setCellValue(attendance.getStudentNumber());
                    row.createCell(3).setCellValue(attendance.isAttendance() ? "O" : "X");
                    row.createCell(4).setCellValue(attendance.getCreatedDate());

                    if (attendance.getSign() != null && !attendance.getSign().isEmpty()) {
                        try {
                            URL signUrl = new URL(attendance.getSign());
                            byte[] bytes = IOUtils.toByteArray(signUrl.openStream());
                            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                            CreationHelper helper = workbook.getCreationHelper();
                            Drawing<?> drawing = sheet.createDrawingPatriarch();
                            ClientAnchor anchor = helper.createClientAnchor();
                            anchor.setCol1(5);
                            anchor.setRow1(rowNum - 1);
                            anchor.setCol2(6);
                            anchor.setRow2(rowNum);
                            Picture pict = drawing.createPicture(anchor, pictureIdx);
                            pict.resize(7,14);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            String fileName = eventName + "_참석자명단.xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
                MultipartFile attendanceListMultipartFile = workbookToMultipartFileConverter.convert(workbook, eventName+"_참석자명단.xlsx");
                return attendanceListMultipartFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close(); // Workbook 닫기
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}