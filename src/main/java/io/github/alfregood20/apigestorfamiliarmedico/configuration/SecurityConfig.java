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
                            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                            configuration.setAllowedMethods(List.of("*"));
                            configuration.setAllowedHeaders(List.of("*"));

                            return configuration;
                        })
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(requests -> requests

                        // Públicos y sesión
                        .requestMatchers("/auth/login", "/auth/registro", "/health").permitAll()
                        .requestMatchers("/auth/me/**", "/auth/logout").authenticated()

                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger", "/api-docs/**").permitAll()

                        // Usuarios / Cuidadores (solo ADMIN)
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // Integrantes (solo lectura para CUIDADOR; todo cambio exclusivo ADMIN)
                        .requestMatchers(HttpMethod.GET, "/integrantes/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers("/integrantes/**").hasRole("ADMIN")

                        // Consultas médicas
                        .requestMatchers(HttpMethod.GET, "/consultas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/consultas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PUT, "/consultas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PATCH, "/consultas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.DELETE, "/consultas/**").hasRole("ADMIN")

                        // Aplicaciones de vacunas
                        .requestMatchers(HttpMethod.GET, "/aplicaciones-vacunas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/aplicaciones-vacunas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PUT, "/aplicaciones-vacunas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.DELETE, "/aplicaciones-vacunas/**").hasRole("ADMIN")

                        // Estudios de laboratorio
                        .requestMatchers(HttpMethod.GET, "/estudios-laboratorio/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/estudios-laboratorio/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PUT, "/estudios-laboratorio/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PATCH, "/estudios-laboratorio/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.DELETE, "/estudios-laboratorio/**").hasRole("ADMIN")

                        // Dashboard
                        .requestMatchers(HttpMethod.GET, "/dashboard/**").hasAnyRole("ADMIN", "CUIDADOR")

                        // Recetas médicas
                        .requestMatchers(HttpMethod.GET, "/recetas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/recetas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PUT, "/recetas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PATCH, "/recetas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.DELETE, "/recetas/**").hasRole("ADMIN")

                        // Alertas
                        .requestMatchers(HttpMethod.GET, "/alertas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PATCH, "/alertas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.DELETE, "/alertas/**").hasRole("ADMIN")

                        // Tomas programadas
                        .requestMatchers(HttpMethod.GET, "/tomas/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PATCH, "/tomas/**").hasAnyRole("ADMIN", "CUIDADOR")

                        // Stock de medicamentos
                        .requestMatchers(HttpMethod.GET, "/stock/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.POST, "/stock/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PUT, "/stock/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.PATCH, "/stock/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(HttpMethod.DELETE, "/stock/**").hasRole("ADMIN")

                        // Médicos
                        .requestMatchers(HttpMethod.GET, "/medicos/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers("/medicos/**").hasRole("ADMIN")

                        // Catálogos
                        .requestMatchers(HttpMethod.GET,
                                "/vacunas/**",
                                "/esquemas-vacunacion/**",
                                "/alergias/**",
                                "/especialidades/**",
                                "/medicamentos/**").hasAnyRole("ADMIN", "CUIDADOR")
                        .requestMatchers(
                                "/vacunas/**",
                                "/esquemas-vacunacion/**",
                                "/alergias/**",
                                "/especialidades/**",
                                "/medicamentos/**").denyAll()

                        .anyRequest().denyAll()
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
