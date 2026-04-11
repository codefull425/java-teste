package br.app.veiculos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI veiculosOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("veiculos-srv")
						.description("API REST de veículos, pneus e vínculos (veículo–pneu–posição). Documentação via OpenAPI 3 e Swagger UI.")
						.version("0.0.1-SNAPSHOT"));
	}
}
