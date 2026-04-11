package br.app.veiculos.controller;

import br.app.veiculos.dto.AplicarPneuResponse;
import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.dto.RemoverPneuResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.dto.VeiculoResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.mapper.PneuMapper;
import br.app.veiculos.service.AplicarPneuService;
import br.app.veiculos.service.RemoverPneuService;
import br.app.veiculos.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

	@Mock
	private VeiculoService veiculoService;

	@Mock
	private AplicarPneuService aplicarPneuService;

	@Mock
	private RemoverPneuService removerPneuService;

	@Mock
	private PneuMapper pneuMapper;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new VeiculoController(veiculoService, aplicarPneuService, removerPneuService, pneuMapper))
				.setValidator(validator)
				.build();
	}

	@Test
	void listar() throws Exception {
		when(veiculoService.listarSemPneus()).thenReturn(List.of(VeiculoResponse.builder().id(1L).placa("ABC").build()));
		mockMvc.perform(get("/api/veiculos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].placa").value("ABC"));
	}

	@Test
	void criar() throws Exception {
		when(veiculoService.criar(any())).thenReturn(VeiculoResponse.builder().id(9L).placa("ZZZ").build());
		String json = """
				{"placa":"ZZZ","marca":"M","quilometragemKm":1,"status":"ATIVO"}
				""";
		mockMvc.perform(post("/api/veiculos").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(9));
	}

	@Test
	void aplicarPneu() throws Exception {
		VeiculoDetalheResponse detalhe = VeiculoDetalheResponse.builder().placa("ABC1D23").build();
		when(veiculoService.buscarComPneus(1L)).thenReturn(detalhe);

		String json = """
				{"pneuId":3,"posicao":"C"}
				""";
		mockMvc.perform(post("/api/veiculos/1/aplicar-pneu").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.mensagem").value(AplicarPneuResponse.MENSAGEM_SUCESSO))
				.andExpect(jsonPath("$.veiculo.placa").value("ABC1D23"));
		verify(aplicarPneuService).aplicar(eq(1L), any());
		verify(veiculoService).buscarComPneus(1L);
	}

	@Test
	void removerPneu() throws Exception {
		Pneu pneu = new Pneu();
		pneu.setId(1L);
		when(removerPneuService.remover(eq(1L), any())).thenReturn(pneu);
		when(pneuMapper.toResponse(pneu)).thenReturn(PneuResponse.builder().id(1L).numeroFogo("188").build());

		mockMvc.perform(post("/api/veiculos/1/remover-pneu").contentType(MediaType.APPLICATION_JSON).content("{\"pneuId\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mensagem").value(RemoverPneuResponse.MENSAGEM_SUCESSO))
				.andExpect(jsonPath("$.pneu.numeroFogo").value("188"));
	}
}
