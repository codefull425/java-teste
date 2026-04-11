package br.app.veiculos.controller;

import br.app.veiculos.dto.ListagemAplicacaoPneuResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.service.ListagemAplicacaoPneuQueryService;
import br.app.veiculos.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ListagemAplicacaoPneuControllerTest {

	@Mock
	private VeiculoService veiculoService;

	@Mock
	private ListagemAplicacaoPneuQueryService listagemAplicacaoPneuQueryService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new ListagemAplicacaoPneuController(veiculoService, listagemAplicacaoPneuQueryService)).build();
	}

	@Test
	void listarTodos() throws Exception {
		when(listagemAplicacaoPneuQueryService.listarTodos()).thenReturn(List.of(ListagemAplicacaoPneuResponse.builder().id(1L).veiculoId(2L).pneuId(3L).posicao("A").build()));
		mockMvc.perform(get("/api/veiculosPneus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].posicao").value("A"));
	}

	@Test
	void buscarVeiculoComPneus() throws Exception {
		when(veiculoService.buscarComPneus(1L)).thenReturn(VeiculoDetalheResponse.builder().placa("ABC1D23").build());
		mockMvc.perform(get("/api/veiculosPneus/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.placa").value("ABC1D23"));
	}
}
