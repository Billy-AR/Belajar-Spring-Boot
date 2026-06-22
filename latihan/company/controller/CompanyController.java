package com.backend.latihan.company.controller;

import com.backend.latihan.dto.CompanyDto;
import com.backend.latihan.company.service.ICompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyController {

    private final ICompanyService companyService;

    @GetMapping(path = "/public",version = "1.0.0")
    public ResponseEntity<List<CompanyDto>> getAllCompanies(){

        return ResponseEntity.ok().body(companyService.getAllCompanies());
    }

    @GetMapping(path = "/admin", version = "1.0")
    public ResponseEntity<List<CompanyDto>> getAllCompaniesForAdmin(){
        List<CompanyDto> companyList = companyService.getAllCompaniesForAdmin();

        return ResponseEntity.status(HttpStatus.OK).body(companyList);
    }

    @PostMapping(path = "/admin", version = "1.0")
    public ResponseEntity<String> createCompany(@RequestBody @Valid CompanyDto companyDto){
        boolean isCreated = companyService.createCompany(companyDto);

        if (isCreated){
            return ResponseEntity.status(HttpStatus.CREATED).body("Request processed successfully");
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request processing failed");
        }
    }



}
