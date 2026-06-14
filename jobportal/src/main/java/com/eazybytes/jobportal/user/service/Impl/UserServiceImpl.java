package com.eazybytes.jobportal.user.service.Impl;

import com.eazybytes.jobportal.constants.ApplicationConstant;
import com.eazybytes.jobportal.dto.UserDto;
import com.eazybytes.jobportal.entity.Company;
import com.eazybytes.jobportal.entity.JobPortalUser;
import com.eazybytes.jobportal.entity.Role;
import com.eazybytes.jobportal.repository.CompanyRepository;
import com.eazybytes.jobportal.repository.JobPortalUserRepository;
import com.eazybytes.jobportal.repository.RoleRepository;
import com.eazybytes.jobportal.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {
    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Optional<UserDto> searchUserByEmail(String email) {
        return userRepository.findJobPortalUserByEmail(email).map(this::mapToUserDto);
    };


    @Transactional
    @Override
    public UserDto elavateToEmployer(Long userId) {
        JobPortalUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " +userId));

        //Check if user is already an employer
        if (ApplicationConstant.ROLE_EMPLOYER.equals(user.getRole().getName())){
            return mapToUserDto(user);
        }

        // Cehck if user is already an admin
        if (ApplicationConstant.ROLE_ADMIN.equals(user.getRole())){
            throw  new RuntimeException(("Cannot elevate admin user to employer role"));
        }

        //Find ROLE_EMPLOYER
        Role employerRole = roleRepository.findRoleByName(ApplicationConstant.ROLE_EMPLOYER).orElseThrow(() -> new RuntimeException("ROLE_EMPLOYER not found"));

        user.setRole(employerRole);

        // JobPortalUser updatedUser = userRepository.save(user);
        /**
         * Why it’s unnecessary
         * findById() returns a managed entity
         * You modify it inside a transaction
         * Dirty checking automatically updates it
         */

        return mapToUserDto(user);
    };


    @Transactional
    @Override
    public UserDto assignCompanyToEmployer(Long userId, Long comapnyId) {
        JobPortalUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if(!ApplicationConstant.ROLE_EMPLOYER.equals(user.getRole().getName())){
            throw new RuntimeException("User must be an employer to be assigned to a company");
        }

        Company company = companyRepository.findById(comapnyId).orElseThrow(() -> new RuntimeException("Company not found with ID: " + comapnyId ));

        user.setCompany(company);

       return mapToUserDto(user);
    };

    private UserDto mapToUserDto(JobPortalUser user){
        UserDto dto = new UserDto();

        BeanUtils.copyProperties(user,dto);
        dto.setUserId(user.getId());

        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);

        dto.setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);

        dto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);
        return dto;
    }
}
