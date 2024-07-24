package checkmate.com.checkmate.global.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class S3Download {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ResponseEntity<byte[]> getObject(String storedFileName, String pdfName) throws IOException {
        String decodedUrl = URLDecoder.decode(storedFileName, StandardCharsets.UTF_8.name());

        String[] urlParts = decodedUrl.split(".com/", 2);
        System.out.println(urlParts[1]);
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, urlParts[1]));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        //String fileName = convertToFriendlyFileName(pdfName);
        //String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");


        String fileName = URLEncoder.encode(pdfName, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    // 파일 이름을 예쁘게 변환하는 메서드
    private String convertToFriendlyFileName(String fileName) {
        // 파일 이름에서 URL 인코딩된 부분을 디코딩
        String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        // 파일 이름에서 경로 접두사 제거 (예: "100/event/2303/")
        String[] parts = decodedFileName.split("/", 4);
        if (parts.length > 3) {
            return parts[3]; // 파일 이름만 반환
        } else {
            return decodedFileName; // 경로가 없으면 전체 파일 이름 반환
        }
    }
}