package com.backend.latihan.dto;

public record LoginResponseDto(String message, UserDto user , String jwtToken) {
}
