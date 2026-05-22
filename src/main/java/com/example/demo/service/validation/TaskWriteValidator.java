package com.example.demo.service.validation;

import com.example.demo.dto.TaskUpsertRequest;

public class TaskWriteValidator {
    public void validateForCreate(TaskUpsertRequest request) {
        if (request.name() == null || request.type() == null) {
            return;
        }
        switch (request.enabled()) {
            case true ->
        }
    }
}
