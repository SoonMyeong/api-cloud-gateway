package com.example.greeting;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/api/greeting")
    public ResponseEntity<String> greeting(HttpServletRequest request) throws InterruptedException {

        return ResponseEntity.ok("Hello I'm Greeting Service");
    }
}
