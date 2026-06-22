package com.backend.latihan.contact.controller;

import com.backend.latihan.constant.ApplicationConstant;
import com.backend.latihan.contact.service.IContactService;
import com.backend.latihan.dto.ContactRequestDto;
import com.backend.latihan.dto.ContactResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactController {
    private final IContactService contactService;

    @PostMapping(path = "public",version = "1.0")
    @Transactional
    public ResponseEntity<String> saveContactMsg(@RequestBody @Valid ContactRequestDto contactRequestDto){
        boolean isSaved = contactService.saveContact(contactRequestDto);
        if(isSaved != false){
            return ResponseEntity.status(HttpStatus.CREATED).body("Contact is successfully created");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request processing failed");
        }
       /* throw new NullPointerException("Test nullpointer");*/

    }

    @GetMapping("/admin")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactMsgs(){
        List<ContactResponseDto> contactResponseDtos = contactService.fetchNewContactsMsgs();
        return ResponseEntity.status(HttpStatus.OK).body(contactResponseDtos);
    }

    @GetMapping("/sort/admin")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactMsgsWithSort(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        List<ContactResponseDto> contactResponseDtos = contactService.fetchNewContactMsgsWithSort(sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(contactResponseDtos);
    }

    @GetMapping("/page/admin")
    public ResponseEntity<Page<ContactResponseDto>> fetchOpenContactMsgsWithPaginationAndSort(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "asc") String sortDir ){
        org.springframework.data.domain.Page<ContactResponseDto> contactResponseDtoPage = contactService.fetchOpenContactMsgsWithPaginationAndSort(pageNumber,pageSize,sortBy,sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(contactResponseDtoPage);
    }

    @PatchMapping("/{id}/status/admin")
    @Transactional
    public ResponseEntity<String> closeContactMsg(@PathVariable String id){
        boolean isUpdated= contactService.closeContactMsg(Long.valueOf(id),ApplicationConstant.CLOSED_MESSAGE);

        if(isUpdated){
            return ResponseEntity.status(HttpStatus.OK).body("Contact message updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update contact message.");
        }
    }

}
