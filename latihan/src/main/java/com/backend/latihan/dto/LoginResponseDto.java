package com.backend.latihan.dto;

import com.backend.latihan.entity.UserDto;

public record LoginResponseDto(String message, UserDto user , String jwtToken) {
}
