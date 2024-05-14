package checkmate.com.checkmate.eventattendanceList.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Component
public class WorkbookToMultipartFileConverter {

    public MultipartFile convert(Workbook workbook, String fileName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        return new MultipartFile() {
            @Override
            public String getName() {
                return fileName;
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return bos.size();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return bos.toByteArray();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return bis;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                new FileOutputStream(dest).write(bos.toByteArray());
            }
        };
    }
}
