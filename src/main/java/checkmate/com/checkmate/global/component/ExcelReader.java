package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.stranger.domain.repository.StrangerRepository;
import checkmate.com.checkmate.stranger.dto.StrangerExcelResponseDto;
import checkmate.com.checkmate.student.domain.repository.StudentRepository;
import checkmate.com.checkmate.student.dto.StudentExcelResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static checkmate.com.checkmate.global.codes.ErrorCode.FILE_READ_FAIL;

@Service
@RequiredArgsConstructor
public class ExcelReader {
    @Autowired
    private final EventAttendanceRepository eventAttendanceRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final StrangerRepository strangerRepository;

    public List<StudentExcelResponseDto> readAttendanceListAboutStudent(File attendanceListFile) throws IOException {
        List<StudentExcelResponseDto> studentExcelResponseDtos = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(attendanceListFile);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row headerRow = rowIterator.next();

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
                case "학과":
                    majorIndex = cell.getColumnIndex();
                    break;
                case "이름":
                    nameIndex = cell.getColumnIndex();
                    break;
                case "휴대전화번호":
                    phoneNumberIndex = cell.getColumnIndex();
                    break;
                case "이메일주소":
                    emailIndex = cell.getColumnIndex();
                    break;
                case "학번/사번":
                    studentNumberIndex = cell.getColumnIndex();
                    break;
            }
        }

        try {
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String major = (majorIndex != -1 && row.getCell(majorIndex) != null && row.getCell(majorIndex).getCellType() == CellType.STRING) ? row.getCell(majorIndex).getStringCellValue() : "";

                String name = (nameIndex != -1 && row.getCell(nameIndex) != null && row.getCell(nameIndex).getCellType() == CellType.STRING) ? row.getCell(nameIndex).getStringCellValue() : "";
                if (name.isEmpty()) continue;

                String phoneNumber = (phoneNumberIndex != -1 && row.getCell(phoneNumberIndex) != null && row.getCell(phoneNumberIndex).getCellType() == CellType.STRING) ? row.getCell(phoneNumberIndex).getStringCellValue() : "";

                String email = (emailIndex != -1 && row.getCell(emailIndex) != null && row.getCell(emailIndex).getCellType() == CellType.STRING) ? row.getCell(emailIndex).getStringCellValue() : "";

                int studentNumber = -1;
                if (studentNumberIndex != -1 && row.getCell(studentNumberIndex) != null && row.getCell(studentNumberIndex).getCellType() == CellType.NUMERIC) {
                    studentNumber = (int) row.getCell(studentNumberIndex).getNumericCellValue();
                } else {
                    continue;
                }

                studentExcelResponseDtos.add(StudentExcelResponseDto.of(name, studentNumber, major, phoneNumber, email));
            }
            workbook.close();
        } catch (Exception e) {
            throw new GeneralException(FILE_READ_FAIL);
        }

        return studentExcelResponseDtos;
    }

    public List<StrangerExcelResponseDto> readAndSaveAttendanceListAboutStranger(File attendanceListFile) throws IOException {
        List<StrangerExcelResponseDto> strangerExcelResponseDtos = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(attendanceListFile);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Row headerRow = rowIterator.next();

        int nameIndex = -1;
        int phoneNumberIndex = -1;
        int emailIndex = -1;
        int affiliationIndex = -1;

        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String headerValue = cell.getStringCellValue();

            switch (headerValue) {
                case "이름":
                    nameIndex = cell.getColumnIndex();
                    break;
                case "휴대전화번호":
                    phoneNumberIndex = cell.getColumnIndex();
                    break;
                case "이메일주소":
                    emailIndex = cell.getColumnIndex();
                    break;
                case "소속":
                    affiliationIndex = cell.getColumnIndex();
                    break;
            }
        }

        while (rowIterator.hasNext()) {
            try {
                Row row = rowIterator.next();

                if (nameIndex == -1 || row.getCell(nameIndex) == null || row.getCell(nameIndex).getCellType() != CellType.STRING) {
                    continue;
                }
                String name = row.getCell(nameIndex).getStringCellValue();

                if (phoneNumberIndex == -1 || row.getCell(phoneNumberIndex) == null || row.getCell(phoneNumberIndex).getCellType() != CellType.STRING) {
                    continue;
                }
                String phoneNumber = row.getCell(phoneNumberIndex).getStringCellValue();

                String email = "";
                if (emailIndex != -1 && row.getCell(emailIndex) != null && row.getCell(emailIndex).getCellType() == CellType.STRING) {
                    email = row.getCell(emailIndex).getStringCellValue();
                }

                String affiliation = "";
                if (affiliationIndex != -1 && row.getCell(affiliationIndex) != null && row.getCell(affiliationIndex).getCellType() == CellType.STRING) {
                    affiliation = row.getCell(affiliationIndex).getStringCellValue();
                }

                strangerExcelResponseDtos.add(StrangerExcelResponseDto.of(name, phoneNumber, email, affiliation));

            } catch (Exception e) {
                throw new GeneralException(FILE_READ_FAIL);
            }
            workbook.close();
        }
        return strangerExcelResponseDtos;
    }
}
