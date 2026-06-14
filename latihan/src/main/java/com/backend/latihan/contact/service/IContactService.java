package com.backend.latihan.contact.service;

import com.backend.latihan.dto.ContactRequestDto;
import com.backend.latihan.dto.ContactResponseDto;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

public interface IContactService {
    boolean saveContact(ContactRequestDto contactRequestDto);


    List<ContactResponseDto> fetchNewContactsMsgs();

    List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir);

    Page<ContactResponseDto> fetchOpenContactMsgsWithPaginationAndSort(int pageNumber, int pageSize, String sortBy, String sortDir);

    boolean closeContactMsg(Long id, String status);


}
