package checkmate.com.checkmate.global;

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
        Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 가져오기

        Iterator<Row> rowIterator = sheet.iterator();
        Row headerRow = rowIterator.next(); // 첫 번째 행(헤더) 가져오기

        // 각 열의 인덱스 저장
        int nameIndex = -1;
        int studentNumberIndex = -1;
        int majorIndex = -1;
        int phoneNumberIndex = -1;
        int emailIndex = -1;

        // 헤더 행의 각 셀을 순회하면서 필드 인덱스 찾기
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
                case "전화번호":
                    phoneNumberIndex = cell.getColumnIndex();
                    break;
                case "이메일":
                    emailIndex = cell.getColumnIndex();
                    break;
            }
        }

        try {
            // 데이터 행을 읽어와서 DB에 저장하기
            while (rowIterator.hasNext()) {
                EventAttendanceList attendanceList = null;
                Row row = rowIterator.next();
                String name = row.getCell(nameIndex).getStringCellValue();
                int studentNumber = (int) row.getCell(studentNumberIndex).getNumericCellValue();
                String major = row.getCell(majorIndex).getStringCellValue();
                String phoneNumber = "";
                String email = "";

                // 전화번호와 이메일이 있는 경우에만 값을 가져오도록 함
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

                // 여기서 각 필드 값을 사용하여 EventAttendanceList 객체 생성
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
