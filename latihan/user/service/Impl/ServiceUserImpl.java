package com.backend.latihan.user.service.Impl;

import com.backend.latihan.Util.ApplicationUtility;
import com.backend.latihan.constant.ApplicationConstant;
import com.backend.latihan.dto.JobDto;
import com.backend.latihan.dto.ProfileDto;
import com.backend.latihan.entity.*;
import com.backend.latihan.dto.UserDto;
import com.backend.latihan.repository.*;
import com.backend.latihan.user.service.IServiceUser;
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
public class ServiceUserImpl implements IServiceUser {
    private final JobPortalUserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final RoleRepository roleRepository;

    private final ProfileRepository profileRepository;

    private final JobRepository jobRepository;

    @Override
    public Optional<UserDto> searchUserByEmail(String email) {
        return userRepository.findJobPortalUserByEmail(email).map(this::mapToUserDto);
    }


    @Transactional
    @Override
    public UserDto elavateToEmployer(Long userId) {
        JobPortalUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        //Check if user is already an employer
        if (ApplicationConstant.ROLE_EMPLOYER.equals(user.getRole().getName())){
            return mapToUserDto(user);
        }

        //Find ROLE_EMPLOYER
        Role employerRole = roleRepository.findRoleByName(ApplicationConstant.ROLE_EMPLOYER).orElseThrow(() -> new RuntimeException("ROLE_EMPLOYER not found"));

        user.setRole(employerRole);

        return mapToUserDto(user);
    }


    @Transactional
    @Override
    public UserDto assignCompanyToEmployer(Long userId, Long companyId) {
        JobPortalUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!ApplicationConstant.ROLE_EMPLOYER.equals(user.getRole().getName())){
            throw new RuntimeException("User must be an employer to be assigned to a company");
        }

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

        user.setCompany(company);

        return mapToUserDto(user);
    }

    @Override
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson, MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Profile profile = user.getProfile();

        if (null == profile){
            profile = new Profile();

            profile.setUser(user);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        //Parse JSON string to ProfileDto
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


    private UserDto mapToUserDto(JobPortalUser user){
        UserDto dto = new UserDto();

        BeanUtils.copyProperties(user,dto);

        dto.setUserId(user.getId() != null ? user.getId() : null);

        dto.setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);

        dto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);

        return dto;
    }

    private Profile mapToProfile(Profile profile, ProfileDto profileDto, MultipartFile profilePicture, MultipartFile resume){
        //Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty())
        {
            try {
                profile.setProfilePicture(profilePicture.getBytes());

                profile.setProfilePictureName(profilePicture.getOriginalFilename());

                profile.setProfilePictureType(profilePicture.getContentType());
            }catch (IOException e){
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }

        //Handle resume upload
        if (resume != null && !resume.isEmpty()){
            try {
                profile.setResume(resume.getBytes());

                profile.setResumeName(resume.getOriginalFilename());

                profile.setResumeType(resume.getContentType());
            }catch (IOException e){
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

}
