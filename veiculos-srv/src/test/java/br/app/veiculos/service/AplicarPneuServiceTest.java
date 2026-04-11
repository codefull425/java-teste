package br.app.veiculos.service;

import br.app.veiculos.dto.AplicarPneuRequest;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.exception.RegraNegocioException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AplicarPneuServiceTest {

	@Mock
	private VeiculoRepository veiculoRepository;

	@Mock
	private PneuRepository pneuRepository;

	@Mock
	private VinculoRepository vinculoRepository;

	@InjectMocks
	private AplicarPneuService service;

	@Test
	@DisplayName("aplicar: veículo inexistente → RecursoNaoEncontradoException")
	void veiculoNaoEncontrado() {
		when(veiculoRepository.findById(50L)).thenReturn(Optional.empty());
		AplicarPneuRequest req = request(1L, "A");
		RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class, () -> service.aplicar(50L, req));
		assertEquals("Veículo não encontrado", ex.getMessage());
		verify(pneuRepository, never()).findById(any());
	}

	@Test
	@DisplayName("aplicar: pneu inexistente → RecursoNaoEncontradoException")
	void pneuNaoEncontrado() {
		Veiculo veiculo = new Veiculo();
		veiculo.setId(1L);
		when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
		when(pneuRepository.findById(9L)).thenReturn(Optional.empty());
		RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class, () -> service.aplicar(1L, request(9L, "A")));
		assertEquals("Pneu não encontrado", ex.getMessage());
		verify(vinculoRepository, never()).findAplicacaoAbertaPorPneuId(any());
	}

	@Test
	@DisplayName("aplicar: pneu já aplicado no mesmo veículo → RegraNegocioException")
	void pneuJaAplicadoNoMesmoVeiculo() {
		Veiculo veiculo = new Veiculo();
		veiculo.setId(1L);
		Pneu pneu = new Pneu();
		pneu.setId(3L);
		when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
		when(pneuRepository.findById(3L)).thenReturn(Optional.of(pneu));
		VeiculoPneu aberta = new VeiculoPneu();
		aberta.setVeiculo(veiculo);
		aberta.setPneu(pneu);
		when(vinculoRepository.findAplicacaoAbertaPorPneuId(3L)).thenReturn(Optional.of(aberta));
		RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.aplicar(1L, request(3L, "X")));
		assertEquals("Este pneu já está aplicado neste veículo.", ex.getMessage());
		verify(vinculoRepository, never()).existePneuAplicadoNaPosicao(any(), any());
	}

	@Test
	@DisplayName("aplicar: pneu já aplicado em outro veículo → RegraNegocioException")
	void pneuJaAplicadoEmOutroVeiculo() {
		Veiculo veiculoDestino = new Veiculo();
		veiculoDestino.setId(1L);
		Veiculo outro = new Veiculo();
		outro.setId(2L);
		Pneu pneu = new Pneu();
		pneu.setId(3L);
		when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoDestino));
		when(pneuRepository.findById(3L)).thenReturn(Optional.of(pneu));
		VeiculoPneu aberta = new VeiculoPneu();
		aberta.setVeiculo(outro);
		aberta.setPneu(pneu);
		when(vinculoRepository.findAplicacaoAbertaPorPneuId(3L)).thenReturn(Optional.of(aberta));
		RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.aplicar(1L, request(3L, "A")));
		assertEquals("Este pneu já está aplicado em outro veículo.", ex.getMessage());
	}

	@Test
	@DisplayName("aplicar: posição ocupada → RegraNegocioException")
	void posicaoOcupada() {
		Veiculo veiculo = new Veiculo();
		veiculo.setId(1L);
		Pneu pneu = new Pneu();
		pneu.setId(3L);
		when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
		when(pneuRepository.findById(3L)).thenReturn(Optional.of(pneu));
		when(vinculoRepository.findAplicacaoAbertaPorPneuId(3L)).thenReturn(Optional.empty());
		when(vinculoRepository.existePneuAplicadoNaPosicao(1L, "A")).thenReturn(true);
		RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.aplicar(1L, request(3L, "A")));
		assertEquals("Já existe pneu aplicado nesta posição neste veículo.", ex.getMessage());
	}

	@Test
	@DisplayName("aplicar: persiste vínculo e marca pneu EM_USO")
	void aplicarComSucesso() {
		Veiculo veiculo = new Veiculo();
		veiculo.setId(1L);
		Pneu pneu = new Pneu();
		pneu.setId(3L);
		when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
		when(pneuRepository.findById(3L)).thenReturn(Optional.of(pneu));
		when(vinculoRepository.findAplicacaoAbertaPorPneuId(3L)).thenReturn(Optional.empty());
		when(vinculoRepository.existePneuAplicadoNaPosicao(1L, "A")).thenReturn(false);
		when(vinculoRepository.save(any(VeiculoPneu.class))).thenAnswer(inv -> inv.getArgument(0));
		when(pneuRepository.save(any(Pneu.class))).thenAnswer(inv -> inv.getArgument(0));

		service.aplicar(1L, request(3L, "  A  "));

		verify(vinculoRepository).save(any(VeiculoPneu.class));
		verify(pneuRepository).save(argThat(p -> AplicarPneuService.STATUS_PNEU_EM_USO.equals(p.getStatus())));
	}

	private static AplicarPneuRequest request(Long pneuId, String posicao) {
		AplicarPneuRequest r = new AplicarPneuRequest();
		r.setPneuId(pneuId);
		r.setPosicao(posicao);
		return r;
	}
}
