package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import com.itextpdf.io.font.FontProgramFactory;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfGenerator {

    public static MultipartFile generateEventAttendanceListPdf(String eventName, List<EventSchedule> eventSchedules) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        /* Local Test
        String fontPath = "src/main/resources/NanumGothic.otf";
        String boldFontPath = "src/main/resources/NanumGothicExtraBold.otf";
        */
        String fontPath = "/usr/share/fonts/nanum/NanumGothic.ttf";
        String boldFontPath = "/usr/share/fonts/nanum/NanumGothicExtraBold.ttf";
        PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createFont(fontPath), "Identity-H", true);
        PdfFont boldFont = PdfFontFactory.createFont(FontProgramFactory.createFont(boldFontPath), "Identity-H", true);

        for (int i = 0; i < eventSchedules.size(); i++) {
            if (i>0)
                document.add(new AreaBreak());
            EventSchedule eventSchedule = eventSchedules.get(i);
            document.setTextAlignment(TextAlignment.CENTER);
            document.add(new Paragraph(eventName + " - " + (i + 1) + "회차(" + eventSchedule.getEventDate() + ")").setFontSize(16).setFont(boldFont));

            Table table = new Table(5);
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

    private static Table createEachAttendanceListPdf(Table table, EventSchedule eventSchedule) throws MalformedURLException {
        float imageWidth = 50;
        float imageHeight = 15;
        Color blueColor = new DeviceRgb(0, 0, 255);

        table.addCell("순번");
        table.addCell("이름");
        table.addCell("학과");
        table.addCell("학번/사번");
        table.addCell("서명");

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
        }
        for (int j = 0; j < 3; j++)
            table.addCell(" ");
        table.addCell("출석자 수");
        table.addCell(String.valueOf(numOfAttendance) + " / " + String.valueOf(n));

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
