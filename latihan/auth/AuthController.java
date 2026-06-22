package com.backend.latihan.auth;

import com.backend.latihan.constant.ApplicationConstant;
import com.backend.latihan.dto.LoginRequestDto;
import com.backend.latihan.dto.LoginResponseDto;
import com.backend.latihan.dto.RegisterRequestDto;
import com.backend.latihan.entity.JobPortalUser;
import com.backend.latihan.entity.Role;
import com.backend.latihan.dto.UserDto;
import com.backend.latihan.repository.JobPortalUserRepository;
import com.backend.latihan.repository.RoleRepository;
import com.backend.latihan.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final CompromisedPasswordChecker compromisedPasswordChecker;

    private final JobPortalUserRepository jobPortalUserRepository;

    private final RoleRepository roleRepository;



    @PostMapping(value = "/login/public",version = "1.0")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto){
        try {
            var resultAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password()));
            //Generate JWT token
            String jwtToken = jwtUtil.generateJwtToken(resultAuthentication);

            var user = new UserDto();

            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(),user,jwtToken));
        } catch (BadCredentialsException ex){
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication Failed");
        } catch (AuthenticationException ex){
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed");
        } catch (Exception ex){
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

//    @LogAspect
    @PostMapping(value = "/register/public", version = "1.0")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequestDto){


    JobPortalUser jobPortalUser = new JobPortalUser();

        BeanUtils.copyProperties(registerRequestDto, jobPortalUser);

        jobPortalUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.password()));

        Role role = roleRepository.findRoleByName(ApplicationConstant.ROLE_JOB_SEEKER).orElseThrow(()-> new IllegalArgumentException("Role not found: " + ApplicationConstant.ROLE_JOB_SEEKER));

        jobPortalUser.setRole(role);

        jobPortalUserRepository.save(jobPortalUser);

        return  ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus status, String message ){
        return ResponseEntity.status(status).body(new LoginResponseDto(message,null,null));
    }

}
