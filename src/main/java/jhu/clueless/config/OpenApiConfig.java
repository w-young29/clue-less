package jhu.clueless.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
    title = "Clue-less",
    version = "v1",
    description = "API for Clue-less"))
public class OpenApiConfig {
}
