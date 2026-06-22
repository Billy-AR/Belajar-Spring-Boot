package com.eazybytes.jobportal.user.service.Impl;

import com.eazybytes.jobportal.constants.ApplicationConstant;
import com.eazybytes.jobportal.dto.*;
import com.eazybytes.jobportal.entity.*;
import com.eazybytes.jobportal.repository.*;
import com.eazybytes.jobportal.user.service.IUserService;
import com.eazybytes.jobportal.util.ApplicationUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {
    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;

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

    @Transactional
    @Override
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson, MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Profile profile = user.getProfile();
        if (null == profile) {
            profile = new Profile();
            profile.setUser(user);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // Parse JSON string to ProfileDto
        ProfileDto profileDto = objectMapper.readValue(profileJson, ProfileDto.class);
        Profile savedProfile = profileRepository.save(mapToProfile(profile, profileDto, profilePicture, resume));
        return mapToProfileDto(savedProfile, false);
    }

    @Override
    public ProfileDto getProfile(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), false);
    }

    @Override
    public ProfileDto getProfilePicture(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), true);
    }

    @Override
    public ProfileDto getResume(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), true);
    }

    @Transactional
    @Override
    public JobDto saveJob(String userEmail, Long jobId) {
        // Validate if user exists
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Validate job exists
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        user.getSavedJobs().add(job);

        // userRepository.save(user);
        return ApplicationUtility.transformJobToDto(job);
    }

    @Transactional
    @Override
    public void unsaveJob(String userEmail, Long jobId) {
        // Validate if user exists
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        // Validate job exists
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        user.getSavedJobs().remove(job);
    }

    @Override
    public List<JobDto> getSavedJobs(String userEmail) {
        // Validate if user exists
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return user.getSavedJobs().stream().map(job -> ApplicationUtility.transformJobToDto(job))
                .collect(Collectors.toList());
    }


    private Profile mapToProfile(Profile profile, ProfileDto profileDto,
                                 MultipartFile profilePicture, MultipartFile resume) {
        // Update text fields
        profile.setJobTitle(profileDto.jobTitle());
        profile.setLocation(profileDto.location());
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());
        // Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePictureType(profilePicture.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }
        // Handle resume upload
        if (resume != null && !resume.isEmpty()) {
            try {
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload resume", e);
            }
        }
        return profile;
    }

    private ProfileDto mapToProfileDto(Profile profile, boolean includeBinaryData) {
        ProfileDto dto;
        if (includeBinaryData) {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), profile.getProfilePicture(),
                    profile.getProfilePictureName(), profile.getProfilePictureType(), profile.getResume(),
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt()
            );
        } else {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), null,
                    profile.getProfilePictureName(), profile.getProfilePictureType(), null,
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt());
        }
        return dto;
    }

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
