package com.backend.latihan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactRequestDto(
        @NotBlank(message = "Email cannot be empty, please fill the email input")
        @Email(message = "Invalid email input")
        String email,
        @NotBlank(message = "Name cannot be empty, please fill the name input")
        @Size(min = 3, max = 30, message = "Name input must be than 3 characters and under equal to 30 characters")
        String name,
        @NotBlank(message = "Message cannot be empty, please fill the message input")
        @Size(min = 5, max = 300, message = "Message input minimum 3 characters and maximal 300 characters")
        String message,
        @NotBlank(message = "Subject cannot be empty, please fill the subject input")
        @Size(min = 5, max = 150, message = "Subject must be between 5 and 150 characters")
        String subject,
        @Pattern(regexp = "Job Seeker|Employer|Other")
        String userType) {
}
