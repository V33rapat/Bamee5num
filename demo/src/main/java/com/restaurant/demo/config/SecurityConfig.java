package com.restaurant.demo.config;

import com.restaurant.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
            // ยกเว้นการตรวจสอบ CSRF สำหรับทุก API ที่เริ่มต้นด้วย /api/
            // เนื่องจาก API เหล่านี้มักจะถูกเรียกจาก JavaScript โดยไม่มี Token
            .ignoringRequestMatchers(
                "/api/**" // <-- ใช้ /api/** เพื่อครอบคลุม /api/cart/**, /api/manager/** ฯลฯ
            ) 
        )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/static/**", "/api/customers/login", "/api/customers/register", "/api/customers/**").permitAll()
                .requestMatchers("/customer/**", "/api/cart/**").permitAll()
                .requestMatchers("/manager/**").permitAll()
                .requestMatchers("/employee-login", "/employee", "/employee-orders").permitAll() 
                .requestMatchers("/api/manager/**", "/api/managers/**", "/api/employees/**", "/api/reports/**", "/api/carts/**", "/api/orders/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")  // Only process /login for customer authentication
                .successHandler(authenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
