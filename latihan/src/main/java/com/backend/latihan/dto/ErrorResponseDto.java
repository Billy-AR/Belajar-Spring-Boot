package com.backend.latihan.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponseDto(String apiPath, HttpStatus httpStatus, String errorMsg, LocalDateTime localDateTime) {
}
