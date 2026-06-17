package com.backend.latihan.contact.service.Impl;


import com.backend.latihan.constant.ApplicationConstant;
import com.backend.latihan.contact.service.IContactService;
import com.backend.latihan.dto.ContactRequestDto;
import com.backend.latihan.dto.ContactResponseDto;
import com.backend.latihan.entity.Contact;
import com.backend.latihan.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements IContactService {

   private final ContactRepository contactRepository;



    @Override
    public boolean saveContact(ContactRequestDto contactRequestDto) {
        boolean result = false;

        Contact contact= contactRepository.save(transformToEntity(contactRequestDto));

        if(contact != null && contact.getId() != null ){
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public List<ContactResponseDto> fetchNewContactsMsgs() {
        List<Contact> contacts = contactRepository.findContactsByStatusOrderByCreatedAtAsc(ApplicationConstant.NEW_MESSAGE);

        List<ContactResponseDto> responseDtos = contacts.stream().map(this::transformToDto).collect(Collectors.toList());

        return responseDtos;

    }

    @Override
    public List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        List<Contact> contacts = contactRepository.findContactsByStatus(ApplicationConstant.NEW_MESSAGE, sort);

        List<ContactResponseDto> responseDtos = contacts.stream().map(this::transformToDto).collect(Collectors.toList());

        return responseDtos;

    }

    @Override
    public Page<ContactResponseDto> fetchOpenContactMsgsWithPaginationAndSort(int pageNumber, int pageSize, String sortBy, String sortDir) {
       Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Contact> contactPage = contactRepository.findContactsByStatus(ApplicationConstant.NEW_MESSAGE, pageable);

        Page<ContactResponseDto> responseDtoPage = contactPage.map(this::transformToDto);

        return responseDtoPage;
    }

    @Override
    public boolean closeContactMsg(Long id, String status) {
        Contact contact= contactRepository.findById(id).orElse(null);
        if (contact == null) {
            return false;
        }else {
            contact.setStatus(status);
            contactRepository.save(contact);
        }
        return true;
    }

    private Contact transformToEntity(ContactRequestDto contactRequestDto){
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactRequestDto, contact);
       /* contact.setCreatedAt(Instant.now());
        contact.setCreatedBy("System");*/
        contact.setStatus("NEW");
        return contact;
    }


    private ContactResponseDto transformToDto(Contact contact){
        ContactResponseDto contactResponseDto = new ContactResponseDto(contact.getId(),contact.getName(), contact.getEmail(),contact.getUserType(), contact.getSubject(), contact.getMessage(), contact.getStatus(), contact.getCreatedAt());
        return contactResponseDto;
    }
}
