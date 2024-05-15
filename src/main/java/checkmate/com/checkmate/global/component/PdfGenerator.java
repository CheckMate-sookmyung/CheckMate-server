package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.util.List;

@Component
public class PdfGenerator {

    public static MultipartFile generateEventAttendanceListPdf(String eventName, List<EventSchedule> eventSchedules) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        PdfFont font = PdfFontFactory.createFont("src/main/java/checkmate/com/checkmate/global/NanumGothic.otf", "Identity-H", true);
        PdfFont boldFont = PdfFontFactory.createFont("src/main/java/checkmate/com/checkmate/global/NanumGothic.otf", "Identity-H", true);
        Color buleColor = new DeviceRgb(0, 0, 255);

        document.setTextAlignment(TextAlignment.CENTER);
        document.add(new Paragraph(eventName + " 전체 출석 현황").setFontSize(16).setFont(boldFont));

        Table table = new Table(5);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setFont(font);
        table.setFontSize(8);
        table.setTextAlignment(TextAlignment.CENTER);

        document.add(createTotalAttendanceListPdf(table, eventSchedules));


        for (int i = 0; i < eventSchedules.size(); i++) {
            document.add(new AreaBreak());
            EventSchedule eventSchedule = eventSchedules.get(i);
            document.setTextAlignment(TextAlignment.CENTER);
            document.add(new Paragraph(eventName + " - " + (i + 1) + "회차(" + eventSchedule.getEventDate() + ")").setFontSize(16).setFont(boldFont));

            table = new Table(6);
            table.setWidth(UnitValue.createPercentValue(100));
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setFont(font);
            table.setFontSize(8);
            table.setTextAlignment(TextAlignment.CENTER);

            document.add(createEachAttendanceListPdf(table, eventSchedule));
        }
        document.close();

        MultipartFile multipartFileOfPDF = convertDocumnetToMultipartFile(baos, eventName);
        return multipartFileOfPDF;
    }

    private static Table createTotalAttendanceListPdf(Table table, List<EventSchedule> eventSchedules) {
        Color redColor = new DeviceRgb(255, 0, 0);

        table.addCell("순번");
        table.addCell("이름");
        table.addCell("학과");
        table.addCell("학번/사번");
        table.addCell("출석률(%)");

        int n = 0;
        for (EventAttendanceList attendee : eventSchedules.get(0).getEventAttendanceLists()) {
            table.addCell(String.valueOf(++n));
            table.addCell(attendee.getName());
            table.addCell(attendee.getMajor());
            table.addCell(String.valueOf(attendee.getStudentNumber()));
            if (attendee.getAttendanceRate() <= 60){
                Cell cell = new Cell();
                cell.add(new Paragraph(String.valueOf(attendee.getAttendanceRate())).setFontColor(redColor));
                table.addCell(cell);
            } else
                table.addCell(String.valueOf(attendee.getAttendanceRate()));
        }
        return table;
    }

    private static Table createEachAttendanceListPdf(Table table, EventSchedule eventSchedule) throws MalformedURLException {
        float imageWidth = 50;
        float imageHeight = 15;

        table.addCell("순번");
        table.addCell("이름");
        table.addCell("학과");
        table.addCell("학번/사번");
        table.addCell("서명");
        table.addCell("출석 시간");

        int n = 0;
        int numOfAttendance = 0;
        for (EventAttendanceList attendee : eventSchedule.getEventAttendanceLists()) {
            table.addCell(String.valueOf(++n));
            table.addCell(attendee.getName());
            table.addCell(attendee.getMajor());
            table.addCell(String.valueOf(attendee.getStudentNumber()));
            if (attendee.getSign() != null) {
                Image signImage = new Image(ImageDataFactory.create(attendee.getSign()));
                signImage.scaleToFit(imageWidth, imageHeight);
                table.addCell(signImage);
                numOfAttendance++;
            } else {
                table.addCell(new Cell());
            }
            String createdDate = attendee.getCreatedDate() != null ? attendee.getCreatedDate().toString() : " ";
            table.addCell(createdDate);
        }
        for (int j = 0; j < 4; j++)
            table.addCell(" ");
        table.addCell("출석자 수");
        table.addCell(String.valueOf(numOfAttendance));

        return table;
    }

    private static MultipartFile convertDocumnetToMultipartFile(ByteArrayOutputStream baos, String eventName){

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray()) {
            @Override
            public String getFilename() {
                return eventName + "_참석자_명단.pdf";
            }
        };

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
