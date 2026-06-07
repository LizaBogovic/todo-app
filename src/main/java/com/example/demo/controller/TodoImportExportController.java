package com.example.demo.controller;

import com.example.demo.dto.TodoExportResponseDto;
import com.example.demo.dto.TodoImportRequestDto;
import com.example.demo.dto.TodoImportResultDto;
import com.example.demo.service.TodoImportExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class TodoImportExportController {
    private final TodoImportExportService importExportService;
    @GetMapping(value = "/api/todos/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TodoExportResponseDto> exportTodos() {
        TodoExportResponseDto body = importExportService.exportTodos();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.json\"")
                .body(body);
    }

    @PostMapping(value = "/api/todos/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TodoImportResultDto> importTodos(@Valid @RequestBody TodoImportRequestDto request) {
        TodoImportResultDto result = importExportService.importTodos(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping(value = "/api/todos/import-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TodoImportResultDto> importTodosFile(@RequestPart("file") MultipartFile file) {
        /*TodoImportResultDto result = importExportService.importTodos(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);*/
        return null;
    }
}
