package com.example.demo.dto;

import java.util.List;

public record TodoImportRequestDto(List<TodoRequestDto> todos) {
}
