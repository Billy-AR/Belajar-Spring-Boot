package com.backend.latihan.company.service.impl;

import com.backend.latihan.constant.ApplicationConstant;
import com.backend.latihan.dto.CompanyDto;
import com.backend.latihan.dto.JobDto;
import com.backend.latihan.entity.Company;
import com.backend.latihan.entity.Job;
import com.backend.latihan.repository.CompanyRepository;
import com.backend.latihan.company.service.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;


    @Override
    public List<CompanyDto> getAllCompanies() {
        List<Company> companyList= companyRepository.findAllWithJobsStatus(ApplicationConstant.ACTIVE_STATUS);
        return companyList.stream().map(this::transformToDto).collect(Collectors.toList());

    }

    @Override
    public List<CompanyDto> getAllCompaniesForAdmin() {
       List<Company> company = companyRepository.findAll();

       List<CompanyDto> companyDtos = company.stream().map(this::transformCompanyToDtoForAdmin).collect(Collectors.toList());

       return companyDtos;
    }

    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public boolean updateCompanyDetails(Long id, CompanyDto companyDto) {
       int result = companyRepository.updateCompanyDetails(id,companyDto.name(),companyDto.logo(),
                companyDto.industry(),companyDto.size(),companyDto.rating(),
                companyDto.locations(),companyDto.founded(),companyDto.description(),
                companyDto.employees(),companyDto.website());
        return result > 0;
    }

    @Override
    public boolean createCompany(CompanyDto companyDto) {

        Company company = transformCompanyDtoToEntity(companyDto);

        Company savedCompany = companyRepository.save(company);
        return  savedCompany.getId() != null && savedCompany.getId() > 0;
    }


    private CompanyDto transformToDto(Company company){
        List<JobDto> jobs = company.getJobs().stream().map(this::transformJobToJobDto).collect(Collectors.toList());

        return new CompanyDto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(),
                jobs
                );
    }

    private JobDto transformJobToJobDto(Job job){
        return new JobDto(     job.getId(),
                job.getTitle(),
                job.getCompany().getId(),
                job.getCompany().getName(),
                job.getCompany().getLogo(),
                job.getLocation(),
                job.getWorkType(),
                job.getJobType(),
                job.getCategory(),
                job.getExperienceLevel(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSalaryCurrency(),
                job.getSalaryPeriod(),
                job.getDescription(),
                job.getRequirements(),
                job.getBenefits(),
                job.getPostedDate(),
                job.getApplicationDeadline(),
                job.getApplicationsCount(),
                job.getFeatured(),
                job.getUrgent(),
                job.getRemote(),
                job.getStatus());
    }

    private Company transformCompanyDtoToEntity(CompanyDto companyDto){
        Company company = new Company();
         BeanUtils.copyProperties(companyDto, company);
       return company;
    }

    private CompanyDto transformCompanyToDtoForAdmin(Company company){
        CompanyDto companyDto = new CompanyDto(company.getId(), company.getName(),company.getLogo(), company.getIndustry(), company.getSize(), company.getRating(), company.getLocations(), company.getFounded(), company.getDescription(), company.getEmployees(), company.getWebsite(),company.getCreatedAt(), null);

        return companyDto;
    }
}
