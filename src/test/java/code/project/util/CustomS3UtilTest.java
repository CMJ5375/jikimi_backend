package code.project.util;

import code.project.util.CustomS3Util; // ← 네 유틸 패키지 확인
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(CustomS3Util.class)
class CustomS3UtilTest {

    private static final Logger log = LoggerFactory.getLogger(CustomS3UtilTest.class);

    @Autowired
    CustomS3Util s3Util;

    @Test
    void upload_temp_file() throws Exception {
        Path tmp = Files.createTempFile("s3test-", ".txt");
        Files.writeString(tmp, "hello S3!");

        log.info("upload test... path={}", tmp);
        s3Util.uploadFiles(List.of(tmp), false);

        assertTrue(Files.exists(tmp), "delFlag=false 이므로 로컬 파일은 남아있어야 함");
    }

    @Test
    void upload_then_delete_by_filename_key() throws Exception {
        Path tmp = Files.createTempFile("s3test-del-", ".txt");
        Files.writeString(tmp, "to be deleted from S3");

        log.info("upload then delete... path={}", tmp);
        s3Util.uploadFiles(List.of(tmp), false);
        s3Util.deleteFiles(List.of(tmp)); // 파일명(key) 기준 삭제

        // 로컬 파일은 우리가 지우지 않았으니 남아있음
        assertTrue(Files.exists(tmp));
    }

    @Test
    void upload_and_delete_local_after() throws Exception {
        Path tmp = Files.createTempFile("s3test-localdel-", ".txt");
        Files.writeString(tmp, "delete local after upload");

        log.info("upload with delFlag=true... path={}", tmp);
        s3Util.uploadFiles(List.of(tmp), true); // 업로드 후 로컬 파일 삭제

        assertTrue(Files.notExists(tmp), "delFlag=true 이므로 로컬 파일은 삭제되어야 함");
    }
}
