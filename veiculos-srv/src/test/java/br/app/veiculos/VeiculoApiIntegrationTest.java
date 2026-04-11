package br.app.veiculos;



import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.MethodOrderer;

import org.junit.jupiter.api.Order;

import org.junit.jupiter.api.Tag;

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



@Tag("integration")
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

	@DisplayName("GET /api/veiculosPneus lista aplicações veículo–pneu (listagem)")

	void listarAplicacoesPneuVeiculo() throws Exception {

		mockMvc.perform(get("/api/veiculosPneus"))

				.andExpect(status().isOk())

				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))

				.andExpect(jsonPath("$[0].veiculoId").exists())

				.andExpect(jsonPath("$[0].pneuId").exists())

				.andExpect(jsonPath("$[0].posicao").exists());

	}



	@Test

	@Order(3)

	@DisplayName("GET /api/veiculosPneus/1 retorna pneus aplicados com posição")

	void buscarVeiculo_comPneus() throws Exception {

		mockMvc.perform(get("/api/veiculosPneus/1"))

				.andExpect(status().isOk())

				.andExpect(jsonPath("$.placa").value("ABC1D23"))

				.andExpect(jsonPath("$.pneusAplicados", hasSize(6)))

				.andExpect(jsonPath("$.pneusAplicados[*].pneuId", hasItem(1)))

				.andExpect(jsonPath("$.pneusAplicados[*].posicao", hasItem("A")))

				.andExpect(jsonPath("$.pneusAplicados[*].numeroDeFogo", hasItem("188")));

	}



	@Test

	@Order(4)

	@DisplayName("POST aplicar pneu na mesma posição retorna conflito")

	void aplicarPneu_posicaoOcupada() throws Exception {

		String body = """

				{"pneuId": 3, "posicao": "A"}

				""";

		mockMvc.perform(post("/api/veiculos/1/aplicar-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isConflict())

				.andExpect(jsonPath("$.detail").value("Já existe pneu aplicado nesta posição neste veículo."));

	}



	@Test

	@Order(5)

	@DisplayName("POST aplicar pneu disponível em posição livre conclui com sucesso")

	void aplicarPneu_sucesso() throws Exception {

		String body = """

				{"pneuId": 3, "posicao": "C"}

				""";

		mockMvc.perform(post("/api/veiculos/1/aplicar-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isCreated())

				.andExpect(jsonPath("$.mensagem").value("PNEU APLICADO COM SUCESSO"))

				.andExpect(jsonPath("$.veiculo.placa").value("ABC1D23"))

				.andExpect(jsonPath("$.veiculo.pneusAplicados", hasSize(7)))

				.andExpect(jsonPath("$.veiculo.pneusAplicados[*].pneuId", hasItem(3)))

				.andExpect(jsonPath("$.veiculo.pneusAplicados[*].posicao", hasItem("C")))

				.andExpect(jsonPath("$.veiculo.pneusAplicados[*].numeroDeFogo", hasItem("190")));



		mockMvc.perform(get("/api/veiculosPneus/1"))

				.andExpect(status().isOk())

				.andExpect(jsonPath("$.pneusAplicados[*].posicao", hasItem("C")));

	}



	@Test

	@Order(6)

	@DisplayName("POST aplicar o mesmo pneu já aplicado no mesmo veículo retorna conflito")

	void aplicarPneu_jaAplicadoNoMesmoVeiculo() throws Exception {

		String body = """

				{"pneuId": 3, "posicao": "H"}

				""";

		mockMvc.perform(post("/api/veiculos/1/aplicar-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isConflict())

				.andExpect(jsonPath("$.detail").value("Este pneu já está aplicado neste veículo."));

	}



	@Test

	@Order(7)

	@DisplayName("POST remover pneu retorna pneu e mensagem de sucesso")

	void removerPneu() throws Exception {

		String body = """

				{"pneuId": 1}

				""";

		mockMvc.perform(post("/api/veiculos/1/remover-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isOk())

				.andExpect(jsonPath("$.mensagem").value("PNEU REMOVIDO COM SUCESSO"))

				.andExpect(jsonPath("$.pneu.id").value(1))

				.andExpect(jsonPath("$.pneu.numeroFogo").value("188"));



		mockMvc.perform(get("/api/veiculosPneus/1"))

				.andExpect(status().isOk())

				.andExpect(jsonPath("$.pneusAplicados[*].numeroDeFogo", not(hasItem("188"))));

	}



	@Test

	@Order(8)

	@DisplayName("POST remover pneu sem aplicação em aberto retorna 404")

	void removerPneu_semAplicacaoEmAberto() throws Exception {

		String body = """

				{"pneuId": 1}

				""";

		mockMvc.perform(post("/api/veiculos/1/remover-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isNotFound())

				.andExpect(jsonPath("$.detail").value("Não há aplicação em aberto deste pneu neste veículo."));

	}



	@Test

	@Order(9)

	@DisplayName("GET /api/pneus lista pneus")

	void listarPneus() throws Exception {

		mockMvc.perform(get("/api/pneus"))

				.andExpect(status().isOk())

				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))

				.andExpect(jsonPath("$[0].numeroFogo").exists())

				.andExpect(jsonPath("$[0].marca").exists());

	}



	@Test

	@Order(10)

	@DisplayName("POST /api/veiculos cria veículo com placa nova")

	void postVeiculos_criar() throws Exception {

		String body = """

				{"placa": "TST1A99", "marca": "Volvo", "quilometragemKm": 1000, "status": "ATIVO"}

				""";

		mockMvc.perform(post("/api/veiculos")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isCreated())

				.andExpect(jsonPath("$.placa").value("TST1A99"))

				.andExpect(jsonPath("$.marca").value("Volvo"))

				.andExpect(jsonPath("$.quilometragemKm").value(1000))

				.andExpect(jsonPath("$.status").value("ATIVO"));

	}



	@Test

	@Order(11)

	@DisplayName("POST /api/veiculos com placa já existente retorna conflito")

	void postVeiculos_conflitoPlaca() throws Exception {

		String body = """

				{"placa": "abc1d23", "marca": "Outra", "quilometragemKm": 1, "status": "ATIVO"}

				""";

		mockMvc.perform(post("/api/veiculos")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isConflict())

				.andExpect(jsonPath("$.detail").value("Já existe veículo com esta placa."));

	}



	@Test

	@Order(12)

	@DisplayName("POST /api/veiculos com corpo inválido retorna 400")

	void postVeiculos_bodyInvalido() throws Exception {

		mockMvc.perform(post("/api/veiculos")

						.contentType(MediaType.APPLICATION_JSON)

						.content("{}"))

				.andExpect(status().isBadRequest())

				.andExpect(jsonPath("$.title").value("Dados inválidos"))

				.andExpect(jsonPath("$.detail", containsString("placa")));

	}



	@Test

	@Order(13)

	@DisplayName("POST /api/pneus cria pneu com número de fogo novo")

	void postPneus_criar() throws Exception {

		String body = """

				{"numeroFogo": "NF-INT-001", "marca": "Michelin", "pressaoAtualPsi": 100.5, "status": "DISPONIVEL"}

				""";

		mockMvc.perform(post("/api/pneus")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isCreated())

				.andExpect(jsonPath("$.numeroFogo").value("NF-INT-001"))

				.andExpect(jsonPath("$.marca").value("Michelin"))

				.andExpect(jsonPath("$.pressaoAtualPsi").value(100.5))

				.andExpect(jsonPath("$.status").value("DISPONIVEL"));

	}



	@Test

	@Order(14)

	@DisplayName("POST /api/pneus com número de fogo duplicado retorna conflito")

	void postPneus_conflitoNumeroFogo() throws Exception {

		String body = """

				{"numeroFogo": "188", "marca": "X", "pressaoAtualPsi": 90.0, "status": "DISPONIVEL"}

				""";

		mockMvc.perform(post("/api/pneus")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isConflict())

				.andExpect(jsonPath("$.detail").value("Já existe pneu com este número de fogo."));

	}



	@Test

	@Order(15)

	@DisplayName("POST /api/pneus com corpo inválido retorna 400")

	void postPneus_bodyInvalido() throws Exception {

		String body = """

				{"numeroFogo": "Z", "marca": "Y", "pressaoAtualPsi": 0, "status": "DISPONIVEL"}

				""";

		mockMvc.perform(post("/api/pneus")

						.contentType(MediaType.APPLICATION_JSON)

						.content(body))

				.andExpect(status().isBadRequest())

				.andExpect(jsonPath("$.title").value("Dados inválidos"))

				.andExpect(jsonPath("$.detail", containsString("pressaoAtualPsi")));

	}



	@Test

	@Order(16)

	@DisplayName("POST aplicar-pneu com corpo inválido retorna 400")

	void postAplicarPneu_bodyInvalido() throws Exception {

		mockMvc.perform(post("/api/veiculos/1/aplicar-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content("{}"))

				.andExpect(status().isBadRequest())

				.andExpect(jsonPath("$.title").value("Dados inválidos"))

				.andExpect(jsonPath("$.detail", containsString("pneuId")));

	}



	@Test

	@Order(17)

	@DisplayName("POST remover-pneu com corpo inválido retorna 400")

	void postRemoverPneu_bodyInvalido() throws Exception {

		mockMvc.perform(post("/api/veiculos/1/remover-pneu")

						.contentType(MediaType.APPLICATION_JSON)

						.content("{}"))

				.andExpect(status().isBadRequest())

				.andExpect(jsonPath("$.title").value("Dados inválidos"))

				.andExpect(jsonPath("$.detail", containsString("pneuId")));

	}

}


