// src/main/java/code/project/controller/RootController.java
package code.project.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class RootController {
    @GetMapping("/")
    public String ping() { return "ok"; } // 200

    @GetMapping("/project/health")
    public String health() { return "ok"; }
}
