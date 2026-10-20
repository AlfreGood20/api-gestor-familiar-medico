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
import org.springframework.web.cors.CorsConfiguration;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

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

                // Configuracion de cors
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowCredentials(true);
                            configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));
                            configuration.setAllowedMethods(List.of("*"));
                            configuration.setAllowedHeaders(List.of("*"));

                            return configuration;
                        })
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/login","/auth/registro").permitAll()
                        .requestMatchers("/auth/me", "/auth/logout").authenticated()

                        // integrantes
                        .requestMatchers(HttpMethod.GET, "/integrantes/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/integrantes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/integrantes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/integrantes/**").hasRole("ADMIN")

                        // usuarios
                        .requestMatchers("/usuarios/cuidadores/**").hasRole("ADMIN")

                        // medicamentos
                        .requestMatchers(HttpMethod.GET, "/medicamentos/**").authenticated()
                        .requestMatchers("/medicamentos/**").denyAll()

                        .anyRequest().permitAll()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // Configuracion de cerrar session
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
