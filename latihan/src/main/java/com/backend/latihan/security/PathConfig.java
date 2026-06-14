package com.backend.latihan.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class PathConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths(){
        return List.of(
                "/api/latihan/auth/login/public",
                "/api/latihan/auth/register/public","/api/latihan/companies/public",   "/api/latihan/contacts/public",
                "/api/swagger-ui.html",
                "/swagger-ui/**",
 "/api/latihan/csrf-token/public",               "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean(name = "adminPaths")
    public List<String> adminPaths(){
        return List.of(
                "/api/latihan/contacts/admin",
                "/api/latihan/contacts/sort/admin","/api/latihan/contacts/${id}/status/admin"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths(){
        return List.of("/api/latihan/**"
                );
    }

}
