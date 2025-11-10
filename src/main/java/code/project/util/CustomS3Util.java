package code.project.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomS3Util {

    // ★ application.yml과 정확히 일치
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region; // ap-northeast-2

    private final S3Client s3Client;

    /** 파일경로 리스트 업로드 (key는 파일명) */
    public void uploadFiles(List<Path> filePaths, boolean delFlag) {
        if (filePaths == null || filePaths.isEmpty()) return;
        for (Path filePath : filePaths) {
            String key = filePath.getFileName().toString();
            upload(filePath, key, delFlag);
        }
    }

    /** Path → 지정 key 업로드 */
    public void upload(Path filePath, String key, boolean delFlag) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(detectContentType(filePath))
                .build();

        s3Client.putObject(req, RequestBody.fromFile(filePath));

        if (delFlag) {
            try { Files.deleteIfExists(filePath); } catch (IOException ignore) {}
        }
    }

    /** byte[] → 지정 key 업로드 (MultipartFile 처리용) */
    public void uploadBytes(byte[] bytes, String key, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(bytes));
    }

    /** 파일경로 리스트 삭제 (파일명 → key) */
    public void deleteFiles(List<Path> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) return;
        for (Path filePath : filePaths) {
            String key = filePath.getFileName().toString();
            deleteByKey(key);
        }
    }

    /** key로 삭제 */
    public void deleteByKey(String key) {
        DeleteObjectRequest del = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(del);
    }

    /** 퍼블릭 버킷 접근 URL */
    public String objectUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private static String detectContentType(Path p) {
        try {
            String c = Files.probeContentType(p);
            return c != null ? c : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
