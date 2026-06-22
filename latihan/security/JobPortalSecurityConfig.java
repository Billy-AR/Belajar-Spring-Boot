package com.backend.latihan.security;

import com.backend.latihan.security.filter.JwtTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JobPortalSecurityConfig {


    @Qualifier("publicPaths")
        private final List<String> publicPaths;

    @Qualifier("securedPaths")
    private final List<String> securedPaths;

    @Qualifier("adminPaths")
    private final List<String>
    adminPaths;

    @Qualifier("employerPaths")
    private final List<String> employerPaths;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http){
        return http.csrf(
                csrfConfig -> csrfConfig.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())).cors(corsConfig -> corsConfig.configurationSource(configurationSource())).authorizeHttpRequests(request -> {publicPaths.forEach(path -> request.requestMatchers(path).permitAll());


securedPaths.forEach(path -> {
    request.requestMatchers(path).authenticated();
});

employerPaths.forEach(path -> request.requestMatchers(path).hasRole("EMPLOYER"));

adminPaths.forEach(path -> request.requestMatchers(path).hasRole("ADMIN"));

request.anyRequest().denyAll();

                        }
        ).addFilterBefore(new JwtTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class).formLogin(withDefaults()).httpBasic(withDefaults()).build();
    }


    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    UserDetailsService userDetailsService(){
        System.out.println(passwordEncoder().encode("samekom"));System.out.println(passwordEncoder().encode("hama"));

        System.out.println(passwordEncoder().matches("samekom","$2a$10$ZBPoGetwOuBUHTcRS69JYeDJaltJqEtUa60HuwoKJ4EnEqfKOWG4K"));

        var user1 = User.builder().username("Madan NotReady").password("$2a$10$ZBPoGetwOuBUHTcRS69JYeDJaltJqEtUa60HuwoKJ4EnEqfKOWG4K").roles("USER").build();

        var user2 = User.builder().username("Madan Atmint").password("$2a$10$BZ11IY0AIqrhH/f9SD.HZeJ2cpIVBhdUMaF5uox.k0yUITBNsJ.P2").roles("ADMIN").build();

                return new InMemoryUserDetailsManager(user1,user2);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider){
       return new ProviderManager(authenticationProvider);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }



}
