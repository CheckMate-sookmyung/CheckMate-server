package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;

import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.stranger.domain.Stranger;
import checkmate.com.checkmate.stranger.domain.StrangerRepository;
import checkmate.com.checkmate.student.domain.Student;
import checkmate.com.checkmate.student.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
    public List<EventAttendance> readAndSaveAttendanceListAboutStudent(File attendanceListFile, EventSchedule eventSchedule) throws IOException {
        List<EventAttendance> eventAttendances = new ArrayList<>();

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
                Row row = rowIterator.next();
                if (nameIndex == -1 || row.getCell(nameIndex) == null || row.getCell(nameIndex).getCellType() != CellType.STRING) {
                    continue;
                }
                String name = row.getCell(nameIndex).getStringCellValue();

                if (studentNumberIndex == -1 || row.getCell(studentNumberIndex) == null) {
                    continue;
                }
                int studentNumber = (int) row.getCell(studentNumberIndex).getNumericCellValue();

                String major = "";
                if (majorIndex != -1 && row.getCell(majorIndex) != null && row.getCell(majorIndex).getCellType() == CellType.STRING) {
                    major = row.getCell(majorIndex).getStringCellValue();
                }

                String phoneNumber = "";
                if (phoneNumberIndex != -1 && row.getCell(phoneNumberIndex) != null && row.getCell(phoneNumberIndex).getCellType() == CellType.STRING) {
                    phoneNumber = row.getCell(phoneNumberIndex).getStringCellValue();
                }

                String email = "";
                if (emailIndex != -1 && row.getCell(emailIndex) != null && row.getCell(emailIndex).getCellType() == CellType.STRING) {
                    email = row.getCell(emailIndex).getStringCellValue();
                }

                Optional<Student> studentOpt = studentRepository.findByStudentNumber(studentNumber);
                Student attendanceStudent;

                if (studentOpt.isPresent()) {
                    attendanceStudent = studentOpt.get();
                } else {
                    attendanceStudent = Student.builder()
                            .studentName(name)
                            .studentNumber(studentNumber)
                            .studentMajor(major)
                            .studentPhoneNumber(phoneNumber)
                            .studentEmail(email)
                            .build();
                    studentRepository.save(attendanceStudent);
                }

                EventAttendance eventAttendance = EventAttendance.builder()
                        .eventSchedule(eventSchedule)
                        .student(attendanceStudent)
                        .attendance(false)
                        .build();

                eventAttendanceRepository.save(eventAttendance);
                eventAttendances.add(eventAttendance);
            }
            workbook.close();
        } catch (Exception e) {
            throw new GeneralException(FILE_READ_FAIL);
        }

        return eventAttendances;
    }


    public List<EventAttendance> readAndSaveAttendanceListAboutStranger(File attendanceListFile, EventSchedule eventSchedule) throws IOException {
        List<EventAttendance> eventAttendances = new ArrayList<>();

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

                Optional<Stranger> strangerOpt = strangerRepository.findByStrangerPhoneNumberAndStrangerName(phoneNumber, name);
                Stranger attendanceStranger;

                if (strangerOpt.isPresent()) {
                    attendanceStranger = strangerOpt.get();
                } else {
                    attendanceStranger = Stranger.builder()
                            .strangerName(name)
                            .strangerPhoneNumber(phoneNumber)
                            .strangerEmail(email)
                            .strangerAffiliation(affiliation)
                            .build();
                    strangerRepository.save(attendanceStranger);
                }

                EventAttendance eventAttendance = EventAttendance.builder()
                        .eventSchedule(eventSchedule)
                        .stranger(attendanceStranger)
                        .attendance(false)
                        .build();

                eventAttendanceRepository.save(eventAttendance);
                eventAttendances.add(eventAttendance);
                workbook.close();

            } catch (Exception e) {
                throw new GeneralException(FILE_READ_FAIL);
            }
        }
        return eventAttendances;
    }
}
