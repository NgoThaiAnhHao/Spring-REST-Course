package com.luv2code.todos.config;

import com.luv2code.todos.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username ->
                userRepository.findByEmail(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("USER NOT FOUND")
                        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // AuthenticationConfiguration is object containing the current authentication configuration.
        // Spring Security automatically builds the AuthenticationManager based on your configuration.
        // Example Flow:
        //      DaoAuthenticationProvider -> UserDetailsService -> PasswordEncoder
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // Set the HTTP Status Code for the response returned to the client.
            // Example:
            //  404 Not Found
            //  500 Internal Server Error
            //  401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());


            // Declare the data type of the returned response as JSON.
            response.setContentType("application/json");


            // TURN OFF POPUP
            // Default SpringSecurity response:
            //  + HTTP/1.1 401 Unauthorized
            //  + WWW-Authenticate: Basic realm="Realm"
            //
            // After setHeader (Override):
            //  + HTTP/1.1 401 Unauthorized
            //  + WWW-Authenticate:
            response.setHeader("WWW-Authenticate", "");

            // Write the body content to the HTTP response:
            // Result:
            //      {
            //          "error": "Unauthorized access"
            //      }
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Config path
        httpSecurity.authorizeHttpRequests(config -> config
                // CONFIG FOR SWAGGER
                .requestMatchers("/api/auth/**","/swagger-ui/**", "/v3/api-docs/**", "/swagger-resource/**",
                        "/webjars/**", "/docs").permitAll()

                // CONFIG FOR ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // CONFIG FOR OTHER REQUEST
                .anyRequest().authenticated()
        );

        // Disable csrf because JWT does not use sessions
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // Configure how to handle authentication failures
        httpSecurity.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint())
        );

        // Say with Spring Security: Does NOT use Sessions (By default Spring Security use Session)
        // STATELESS: The login state is not saved on the server.
        //  + Do not create sessions
        //  + Do not save login state
        //  + Do not use JSESSIONID
        httpSecurity.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Add JWT filters to the Security Filter Chain run BEFORE UsernamePasswordAuthenticationFilter
        //
        // JWT requests (Authorization: Bearer token) are handled by jwtAuthenticationFilter.
        // UsernamePasswordAuthenticationFilter is only for login requests using username/password.
        //
        // Login flow:
        // POST /login
        // → UsernamePasswordAuthenticationFilter
        // → AuthenticationManager
        // → Generate JWT token
        //
        // Authenticated request flow:
        // Authorization: Bearer <token>
        // → jwtAuthenticationFilter validates token
        // → Set Authentication into SecurityContext
        //
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        // so Spring can authenticate JWT requests before default authentication processing.
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
