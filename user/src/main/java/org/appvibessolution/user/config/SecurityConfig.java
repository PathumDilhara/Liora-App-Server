package org.appvibessolution.user.config;

import org.appvibessolution.user.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(
            JWTFilter jwtFilter,
            MyUserDetailsService myUserDetailsService
    ) {
        this.jwtFilter = jwtFilter;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return  httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request->request
                        .requestMatchers("/api/v1/user/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers-> headers
                        .xssProtection(withDefaults -> {}) // X-XSS-Protection
                        .contentSecurityPolicy(csp->csp
                        .policyDirectives("script-src 'self'")) // CSP
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentTypeOptions(withDetails -> {}) // X-Content-Type-Options: nosniff
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());

        return builder.build(); // ✅ Now this works!
    }
}



//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        return  new DaoAuthenticationProvider(){{
//            setUserDetailsService(myUserDetailsService);
//            setPasswordEncoder(passwordEncoder());
//        }}
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
//            throws Exception {
//        return configuration.getAuthenticationManager();
//    }

