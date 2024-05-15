package checkmate.com.checkmate.eventattendanceList.service;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.List;

@Component
public class PdfGenerator {

    public static MultipartFile generateEventAttendanceListPdf(String eventName, List<EventSchedule> eventSchedules) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        float imageWidth = 50;
        float imageHeight = 25;
        PdfFont font = PdfFontFactory.createFont("src/main/java/checkmate/com/checkmate/global/NanumGothic.otf", "Identity-H", true);
        PdfFont boldFont = PdfFontFactory.createFont("src/main/java/checkmate/com/checkmate/global/NanumGothic.otf", "Identity-H", true);

        for (int i = 0; i < eventSchedules.size(); i++) {
            EventSchedule eventSchedule = eventSchedules.get(i);
            document.add(new Paragraph(eventName + " - " + (i + 1) + "회차(" + eventSchedule.getEventDate() + ")").setFontSize(16).setFont(boldFont));
            document.setTextAlignment(TextAlignment.CENTER);

            Table table = new Table(6);
            table.setFont(font);
            table.setTextAlignment(TextAlignment.CENTER);

            table.addCell("이름");
            table.addCell("학과");
            table.addCell("학번");
            table.addCell("참석여부");
            table.addCell("싸인");
            table.addCell("참석시간");

            for (EventAttendanceList attendee : eventSchedule.getEventAttendanceLists()) {
                table.addCell(attendee.getName());
                table.addCell(attendee.getMajor());
                table.addCell(String.valueOf(attendee.getStudentNumber()));
                table.addCell(attendee.isAttendance() ? "O" : "X");
                if (attendee.getSign() != null) {
                    Image signImage = new Image(ImageDataFactory.create(attendee.getSign()));
                    signImage.scaleToFit(imageWidth, imageHeight);
                    table.addCell(signImage);
                } else {
                    table.addCell(new Cell());
                }
                String createdDate = attendee.getCreatedDate() != null ? attendee.getCreatedDate().toString() : " ";
                table.addCell(createdDate);
            }

            document.add(table);
            document.add(new AreaBreak());
        }
        document.close();

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray()) {
            @Override
            public String getFilename() {
                return eventName + "_참석자_명단.pdf"; // 파일 이름 지정
            }
        };

        // ByteArrayResource에서 InputStream을 얻어와 MultipartFile로 변환
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return resource.getFilename();
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public boolean isEmpty() {
                return resource.contentLength() == 0;
            }

            @Override
            public long getSize() {
                return resource.contentLength();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return resource.getInputStream().readAllBytes();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(baos.toByteArray());
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                throw new UnsupportedOperationException();
            }
        };

        return multipartFile;
    }
}
