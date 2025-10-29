package code.project.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class FileController {

    // 파일 다운로드 컨트롤러
    @GetMapping("/files/{postId}/{fileName:.+}")
    public void downloadFile(
            @PathVariable("postId") Long postId,
            @PathVariable("fileName") String fileName,
            HttpServletResponse response
    ) throws IOException {

        // 파일이 저장된 실제 경로 (JPostController의 upload 경로와 반드시 일치해야 함)
        String uploadRootPath = System.getProperty("user.dir")
                + File.separator + "uploads"
                + File.separator + postId;

        File file = new File(uploadRootPath, fileName);

        // 파일 존재 확인
        if (!file.exists()) {
            log.error("❌ 파일 없음: {}", file.getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        log.info("📂 파일 다운로드 요청: {}", file.getAbsolutePath());

        // 응답 헤더 설정
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\""
        );

        // 파일 스트림으로 전송
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            StreamUtils.copy(in, out);
            out.flush();
        }

        log.info("✅ 파일 다운로드 완료: {}", fileName);
    }
}