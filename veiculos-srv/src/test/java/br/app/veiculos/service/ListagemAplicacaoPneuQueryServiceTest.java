package br.app.veiculos.service;

import br.app.veiculos.dto.ListagemAplicacaoPneuResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.mapper.ListagemAplicacaoPneuMapper;
import br.app.veiculos.repository.VinculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListagemAplicacaoPneuQueryServiceTest {

	@Mock
	private VinculoRepository vinculoRepository;

	@Mock
	private ListagemAplicacaoPneuMapper listagemAplicacaoPneuMapper;

	@InjectMocks
	private ListagemAplicacaoPneuQueryService service;

	@Test
	void listarTodos() {
		VeiculoPneu vp = new VeiculoPneu();
		vp.setVeiculo(new Veiculo());
		vp.setPneu(new Pneu());
		when(vinculoRepository.findAllFetchVeiculoEPneu()).thenReturn(List.of(vp));
		ListagemAplicacaoPneuResponse dto = ListagemAplicacaoPneuResponse.builder().id(1L).build();
		when(listagemAplicacaoPneuMapper.toResponse(vp)).thenReturn(dto);

		List<ListagemAplicacaoPneuResponse> out = service.listarTodos();
		assertEquals(1, out.size());
		assertEquals(1L, out.get(0).getId());
		verify(vinculoRepository).findAllFetchVeiculoEPneu();
	}
}
