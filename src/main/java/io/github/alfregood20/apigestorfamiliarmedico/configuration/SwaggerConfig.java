package io.github.alfregood20.apigestorfamiliarmedico.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI personalizacionInfo(){
        return new OpenAPI()
                .info(new Info()
                        .title("Api gestor familiar medico")
                        .description("Documentacion intereactiva para endpoints para el control de informacion familiar medico. ")
                        .contact(new Contact()
                                .name("github: AlfreGood20")
                                .url("https://github.com/AlfreGood20/api-gestor-familiar-medico")
                        )
                        .version("v.1")
                )
                .servers(
                        List.of(
                                new Server().url("https://api-gestor-familiar.onrender.com").description("Entorno Produccion"),
                                new Server().url("http://localhost:8080").description("Entorno Local/pruebas")
                        )
                );
    }
}
