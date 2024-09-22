package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.global.domain.CsvResultDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CsvReader {

    final ExcelGenerator excelGenerator;

    public CsvReader(ExcelGenerator excelGenerator) {
        this.excelGenerator = excelGenerator;
    }


    public CsvResultDto parseCsvFile(MultipartFile file) {
        Map<Integer, Integer> studentTimeMap = new HashMap<>();
        List<Map<String, String>> failedRows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    String[] columns = line.split(",");
                    String nameWithId = columns[0];
                    int timeInMinutes = Integer.parseInt(columns[4]);

                    int studentId = extractStudentNumber(nameWithId);

                    studentTimeMap.merge(studentId, timeInMinutes, Integer::sum);
                } catch (Exception e) {
                    String[] columns = line.split(",");
                    Map<String, String> failedRow = new HashMap<>();
                    failedRow.put("name", columns[0]);
                    failedRow.put("accessTime", columns[4]);
                    failedRows.add(failedRow);                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CsvResultDto.of(studentTimeMap, failedRows);
    }

    private int extractStudentNumber(String nameWithId) {
        String idString = nameWithId.replaceAll("[^0-9]", "");
        return Integer.parseInt(idString);
    }

}
