package br.app.veiculos.service;

import br.app.veiculos.dto.RemoverPneuRequest;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.repository.PneuRepository;
import br.app.veiculos.repository.VeiculoRepository;
import br.app.veiculos.repository.VinculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverPneuServiceTest {

	@Mock
	private VeiculoRepository veiculoRepository;

	@Mock
	private PneuRepository pneuRepository;

	@Mock
	private VinculoRepository vinculoRepository;

	@InjectMocks
	private RemoverPneuService service;

	@Test
	@DisplayName("remover: veículo inexistente → RecursoNaoEncontradoException")
	void veiculoNaoEncontrado() {
		when(veiculoRepository.existsById(99L)).thenReturn(false);
		RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class, () -> service.remover(99L, request(1L)));
		assertEquals("Veículo não encontrado", ex.getMessage());
		verify(vinculoRepository, never()).findAplicacaoAbertaPorVeiculoEPneu(any(), any());
	}

	@Test
	@DisplayName("remover: sem aplicação em aberto → RecursoNaoEncontradoException")
	void semAplicacaoEmAberto() {
		when(veiculoRepository.existsById(1L)).thenReturn(true);
		when(vinculoRepository.findAplicacaoAbertaPorVeiculoEPneu(1L, 5L)).thenReturn(Optional.empty());
		RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class, () -> service.remover(1L, request(5L)));
		assertEquals("Não há aplicação em aberto deste pneu neste veículo.", ex.getMessage());
		verify(pneuRepository, never()).save(any());
	}

	@Test
	@DisplayName("remover: encerra vínculo e marca pneu DISPONIVEL")
	void removerComSucesso() {
		when(veiculoRepository.existsById(1L)).thenReturn(true);
		Pneu pneu = new Pneu();
		pneu.setId(2L);
		VeiculoPneu registro = new VeiculoPneu();
		registro.setPneu(pneu);
		when(vinculoRepository.findAplicacaoAbertaPorVeiculoEPneu(1L, 2L)).thenReturn(Optional.of(registro));
		when(vinculoRepository.save(any(VeiculoPneu.class))).thenAnswer(inv -> inv.getArgument(0));
		when(pneuRepository.save(any(Pneu.class))).thenAnswer(inv -> inv.getArgument(0));

		Pneu out = service.remover(1L, request(2L));

		assertNotNull(registro.getDataDesvinculo());
		assertEquals(RemoverPneuService.STATUS_PNEU_DISPONIVEL, out.getStatus());
		verify(vinculoRepository).save(registro);
		verify(pneuRepository).save(pneu);
	}

	private static RemoverPneuRequest request(Long pneuId) {
		RemoverPneuRequest r = new RemoverPneuRequest();
		r.setPneuId(pneuId);
		return r;
	}
}
