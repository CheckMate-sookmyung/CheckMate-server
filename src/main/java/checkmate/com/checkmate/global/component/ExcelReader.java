package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendanceList.domain.repository.EventAttendanceListRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.global.exception.GeneralException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static checkmate.com.checkmate.global.codes.ErrorCode.FILE_READ_FAIL;

@Component
public class ExcelReader {

    public static List<EventAttendanceList> readAndSaveAttendanceList(EventAttendanceListRepository eventAttendanceListRepository, File attendanceFile, EventSchedule schedule) throws IOException {
        EventSchedule eventSchedule = schedule;
        List<EventAttendanceList> eventAttendanceLists = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(attendanceFile);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row headerRow = rowIterator.next();

        // 각 열의 인덱스 저장
        int nameIndex = -1;
        int studentNumberIndex = -1;
        int majorIndex = -1;
        int phoneNumberIndex = -1;
        int emailIndex = -1;

        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String headerValue = cell.getStringCellValue();

            switch (headerValue) {
                case "이름":
                    nameIndex = cell.getColumnIndex();
                    break;
                case "학번/사번":
                    studentNumberIndex = cell.getColumnIndex();
                    break;
                case "학과":
                    majorIndex = cell.getColumnIndex();
                    break;
                case "휴대전화번호":
                    phoneNumberIndex = cell.getColumnIndex();
                    break;
                case "이메일주소":
                    emailIndex = cell.getColumnIndex();
                    break;
            }
        }

        try {
            while (rowIterator.hasNext()) {
                EventAttendanceList attendanceList = null;
                Row row = rowIterator.next();
                String name = row.getCell(nameIndex).getStringCellValue();
                int studentNumber = (int) row.getCell(studentNumberIndex).getNumericCellValue();
                String major = row.getCell(majorIndex).getStringCellValue();
                String phoneNumber = "";
                String email = "";

                if (phoneNumberIndex != -1) {
                    Cell phoneCell = row.getCell(phoneNumberIndex);
                    if (phoneCell != null) {
                        phoneNumber = phoneCell.getStringCellValue();
                    }
                }

                if (emailIndex != -1) {
                    Cell emailCell = row.getCell(emailIndex);
                    if (emailCell != null) {
                        email = emailCell.getStringCellValue();
                    }
                }

                attendanceList = EventAttendanceList.builder()
                        .name(name)
                        .studentNumber(studentNumber)
                        .major(major)
                        .phoneNumber(phoneNumber)
                        .email(email)
                        .eventSchedule(eventSchedule)
                        .build();
                eventAttendanceListRepository.save(attendanceList);
                eventAttendanceLists.add(attendanceList);
            }
            workbook.close();

            return eventAttendanceLists;
        } catch (Exception e) {
            throw new GeneralException(FILE_READ_FAIL);
        }
    }

}
