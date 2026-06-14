package com.eazybytes.jobportal.util;

import com.eazybytes.jobportal.constants.ApplicationConstant;
import com.eazybytes.jobportal.entity.JobPortalUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||  authentication.getPrincipal().equals("anonymousUser")){
                return ApplicationConstant.SYSTEM;
            }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof JobPortalUser jobPortalUser){
            username = jobPortalUser.getEmail();
        }else {
            username= principal.toString(); //fallback
        }
        return username;
    }

}
