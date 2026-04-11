package br.app.veiculos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VeiculoApiIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
			.withDatabaseName("veiculos_test")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void datasourceProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	@DisplayName("GET /api/veiculos lista sem coleção de pneus")
	void listarVeiculos_semPneusNoJson() throws Exception {
		mockMvc.perform(get("/api/veiculos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$[0].placa").exists())
				.andExpect(jsonPath("$[0].pneusAplicados").doesNotExist());
	}

	@Test
	@Order(2)
	@DisplayName("GET /api/veiculos/1 retorna pneus aplicados com posição")
	void buscarVeiculo_comPneus() throws Exception {
		mockMvc.perform(get("/api/veiculos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.placa").value("ABC1D23"))
				.andExpect(jsonPath("$.pneusAplicados", hasSize(2)))
				.andExpect(jsonPath("$.pneusAplicados[*].posicao", hasItem("A")))
				.andExpect(jsonPath("$.pneusAplicados[*].numeroFogo", hasItem("188")));
	}

	@Test
	@Order(3)
	@DisplayName("POST vincular na mesma posição retorna conflito")
	void vincular_posicaoOcupada() throws Exception {
		String body = """
				{"pneuId": 3, "posicao": "A"}
				""";
		mockMvc.perform(post("/api/veiculos/1/vinculos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isConflict());
	}

	@Test
	@Order(4)
	@DisplayName("POST vincular pneu disponível em posição livre cria vínculo")
	void vincular_sucesso() throws Exception {
		String body = """
				{"pneuId": 3, "posicao": "C"}
				""";
		mockMvc.perform(post("/api/veiculos/1/vinculos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/veiculos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pneusAplicados[*].posicao", hasItem("C")));
	}

	@Test
	@Order(5)
	@DisplayName("POST desvincular remove vínculo ativo")
	void desvincular() throws Exception {
		String body = """
				{"pneuId": 1}
				""";
		mockMvc.perform(post("/api/veiculos/1/desvinculos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/veiculos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pneusAplicados[*].numeroFogo", not(hasItem("188"))));
	}

	@Test
	@Order(6)
	@DisplayName("GET /api/pneus lista pneus")
	void listarPneus() throws Exception {
		mockMvc.perform(get("/api/pneus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$[0].numeroFogo").exists())
				.andExpect(jsonPath("$[0].marca").exists());
	}
}
