package com.backend.latihan.aspects;


import com.backend.latihan.dto.LoginResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingSuccessAuditAspect {

    @AfterReturning(
            pointcut = "execution(* com.backend.latihan.auth.AuthController.apiLogin(..))",
            returning = "response"
    )
    public void logSuccessfullLogin(JoinPoint joinPoint, Object response){
        if(!(response instanceof ResponseEntity<?> responseEntity)){
            return;
        }

        Object body = responseEntity.getBody();

        if(!(body instanceof LoginResponseDto loginResponse)){
            return;
        }

        //Only log if login is really successful
        if (loginResponse.user() != null){
            String username = loginResponse.user().getEmail();
            String role = loginResponse.user().getRole();
            log.info("LOGIN SUCCESS | User: {} | Role {}", username, role);
        }
    }
}
