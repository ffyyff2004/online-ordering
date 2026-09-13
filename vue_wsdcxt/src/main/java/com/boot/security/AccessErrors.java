package com.boot.security;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class AccessErrors {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handle(ResponseStatusException exception) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("success", false);
        body.put("code", 0);
        body.put("message", exception.getReason());
        return ResponseEntity.status(exception.getStatus()).body(body);
    }
}

