package com.backend.latihan.Util;

import com.backend.latihan.dto.JobDto;
import com.backend.latihan.entity.Job;
import com.backend.latihan.entity.JobPortalUser;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser"));

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof JobPortalUser jobPortalUser){
            username = jobPortalUser.getName();
        }else {
            username = principal.toString(); //fallback
        }
        return username;
    }

    public static JobDto transformJobToDto(Job  job){
        return new JobDto(
                job.getId(),
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
                job.getStatus()
        );
    }




}
