package io.github.alfregood20.apigestorfamiliarmedico.configuration;

import io.github.alfregood20.apigestorfamiliarmedico.exception.CustomAccessDeniedHandler;
import io.github.alfregood20.apigestorfamiliarmedico.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsServic userDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        return http
                .csrf(csrf -> csrf.disable())

                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()

                        // integrantes
                        .requestMatchers(HttpMethod.GET, "/integrantes/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/integrantes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/integrantes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/integrantes/**").hasRole("ADMIN")

                        // usuarios
                        .requestMatchers(HttpMethod.POST, "/usuarios/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // Manejo para cerrar session
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            response.setStatus(HttpStatus.NO_CONTENT.value());
                        }))
                )

                .userDetailsService(userDetailsService)

                .exceptionHandling(exh -> exh
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                .build();
    }
}
