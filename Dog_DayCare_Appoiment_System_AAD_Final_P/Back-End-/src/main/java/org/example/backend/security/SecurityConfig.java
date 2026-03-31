package org.example.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter,
                                                   UserDetailsServiceImpl userDetailsService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public ──────────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password/request-otp", "/api/v1/auth/forgot-password/reset").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/v1/health-reports/qr/**").permitAll()  // QR scan
                .requestMatchers(HttpMethod.GET,  "/api/v1/health-reports/qr-ui/**").permitAll()  // QR UI
                .requestMatchers(HttpMethod.GET, "/api/v1/users/public/by-role").permitAll()

                // ── User management (ADMIN only) ─────────────────────────────
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                // ── Dog management ───────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/dogs").hasAnyRole("PET_OWNER", "ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/v1/dogs/**").hasAnyRole("PET_OWNER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/dogs/**").hasAnyRole("PET_OWNER", "ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/v1/dogs/**").authenticated()

                // ── Daycare appointments ─────────────────────────────────────
                .requestMatchers(HttpMethod.POST,  "/api/v1/daycare-appointments").hasAnyRole("PET_OWNER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/daycare-appointments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,   "/api/v1/daycare-appointments/**").hasAnyRole("ADMIN", "CARETAKER", "PET_OWNER")

                // ── Vaccinations ─────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/vaccinations").hasAnyRole("ADMIN", "CARETAKER")
                .requestMatchers(HttpMethod.GET,  "/api/v1/vaccinations/**").authenticated()

                // ── Health reports ───────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/health-reports").hasAnyRole("ADMIN", "CARETAKER")
                .requestMatchers(HttpMethod.GET,  "/api/v1/health-reports/**").authenticated()

                // ── Doctor appointments ──────────────────────────────────────
                .requestMatchers("/api/v1/doctor-appointments/**").hasAnyRole("PET_OWNER", "DOCTOR", "ADMIN")

                // ── Notifications ────────────────────────────────────────────
                .requestMatchers("/api/v1/notifications/**").hasAnyRole("PET_OWNER", "ADMIN")

                .anyRequest().authenticated()
            )
            .authenticationProvider(daoAuthProvider(userDetailsService))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

