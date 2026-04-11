package br.app.veiculos.controller;

import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.service.PneuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PneuControllerTest {

	@Mock
	private PneuService pneuService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new PneuController(pneuService))
				.setValidator(validator)
				.build();
	}

	@Test
	void listar() throws Exception {
		when(pneuService.listar()).thenReturn(List.of(PneuResponse.builder().id(1L).numeroFogo("188").build()));
		mockMvc.perform(get("/api/pneus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].numeroFogo").value("188"));
	}

	@Test
	void criar() throws Exception {
		PneuResponse body = PneuResponse.builder().id(2L).numeroFogo("NEW").marca("M").pressaoAtualPsi(new BigDecimal("90")).status("DISPONIVEL").build();
		when(pneuService.criar(any())).thenReturn(body);

		String json = """
				{"numeroFogo":"NEW","marca":"M","pressaoAtualPsi":90,"status":"DISPONIVEL"}
				""";
		mockMvc.perform(post("/api/pneus").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.numeroFogo").value("NEW"));
		verify(pneuService).criar(any());
	}
}
