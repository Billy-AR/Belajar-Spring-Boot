package com.backend.latihan.company.service;


import com.backend.latihan.dto.CompanyDto;
import com.backend.latihan.entity.Company;

import java.util.List;

public interface ICompanyService {
    List<CompanyDto> getAllCompanies();

    List<CompanyDto> getAllCompaniesForAdmin();

    void deleteCompanyById(Long id);

    boolean updateCompanyDetails(Long id, CompanyDto companyDto);

    boolean createCompany(CompanyDto companyDto);

}
