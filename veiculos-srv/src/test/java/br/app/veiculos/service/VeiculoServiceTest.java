package br.app.veiculos.service;

import br.app.veiculos.dto.PneuAplicadoResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.dto.VeiculoRequest;
import br.app.veiculos.dto.VeiculoResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.mapper.VeiculoMapper;
import br.app.veiculos.repository.VeiculoRepository;
import br.app.veiculos.repository.VinculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

	@Mock
	private VeiculoRepository veiculoRepository;

	@Mock
	private VinculoRepository vinculoRepository;

	@Mock
	private VeiculoMapper veiculoMapper;

	@InjectMocks
	private VeiculoService veiculoService;

	@Test
	void listarSemPneus() {
		Veiculo v = new Veiculo();
		v.setId(1L);
		when(veiculoRepository.findAll()).thenReturn(List.of(v));
		when(veiculoMapper.toVeiculoResponse(v)).thenReturn(VeiculoResponse.builder().id(1L).placa("X").build());

		List<VeiculoResponse> out = veiculoService.listarSemPneus();
		assertEquals(1, out.size());
		assertEquals(1L, out.get(0).getId());
	}

	@Test
	void buscarComPneus_filtraSomenteAbertos() {
		Veiculo veiculo = new Veiculo();
		veiculo.setId(1L);
		VeiculoPneu aberto = new VeiculoPneu();
		aberto.setDataDesvinculo(null);
		Pneu p = new Pneu();
		p.setId(9L);
		p.setNumeroFogo("188");
		aberto.setPneu(p);
		aberto.setPosicao("A");
		VeiculoPneu fechado = new VeiculoPneu();
		fechado.setDataDesvinculo(LocalDateTime.now());
		veiculo.setAplicacoes(List.of(aberto, fechado));

		when(vinculoRepository.findVeiculoPneuPosicao(1L)).thenReturn(Optional.of(veiculo));
		PneuAplicadoResponse aplicado = PneuAplicadoResponse.builder().pneuId(9L).build();
		when(veiculoMapper.toPneuAplicado(aberto)).thenReturn(aplicado);
		VeiculoDetalheResponse detalhe = VeiculoDetalheResponse.builder().id(1L).build();
		when(veiculoMapper.toDetalhe(eq(veiculo), eq(List.of(aplicado)))).thenReturn(detalhe);

		assertSame(detalhe, veiculoService.buscarComPneus(1L));
		verify(veiculoMapper, never()).toPneuAplicado(fechado);
	}

	@Test
	void buscarComPneus_veiculoNaoEncontrado() {
		when(vinculoRepository.findVeiculoPneuPosicao(99L)).thenReturn(Optional.empty());
		assertThrows(RecursoNaoEncontradoException.class, () -> veiculoService.buscarComPneus(99L));
	}

	@Test
	void criar_sucesso() {
		VeiculoRequest req = new VeiculoRequest();
		req.setPlaca("  abc1  ");
		req.setMarca("  Volvo ");
		req.setQuilometragemKm(10);
		req.setStatus(" ativo ");

		when(veiculoRepository.existsByPlacaIgnoreCase("ABC1")).thenReturn(false);
		Veiculo salvo = new Veiculo();
		salvo.setId(5L);
		when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));
		when(veiculoMapper.toVeiculoResponse(any(Veiculo.class))).thenReturn(VeiculoResponse.builder().id(5L).placa("ABC1").build());

		VeiculoResponse res = veiculoService.criar(req);
		assertEquals(5L, res.getId());
		verify(veiculoRepository).save(argThat(v -> "ABC1".equals(v.getPlaca()) && "Volvo".equals(v.getMarca()) && "ATIVO".equals(v.getStatus()) && v.getQuilometragemKm() == 10));
	}

	@Test
	void criar_placaDuplicada() {
		VeiculoRequest req = new VeiculoRequest();
		req.setPlaca("x");
		req.setMarca("m");
		req.setQuilometragemKm(0);
		req.setStatus("ATIVO");
		when(veiculoRepository.existsByPlacaIgnoreCase("X")).thenReturn(true);
		assertThrows(RegraNegocioException.class, () -> veiculoService.criar(req));
		verify(veiculoRepository, never()).save(any());
	}

	@Test
	void criar_placaNull_normalizaVazio() {
		VeiculoRequest req = new VeiculoRequest();
		req.setPlaca(null);
		req.setMarca("m");
		req.setQuilometragemKm(0);
		req.setStatus("ATIVO");
		when(veiculoRepository.existsByPlacaIgnoreCase("")).thenReturn(false);
		when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));
		when(veiculoMapper.toVeiculoResponse(any(Veiculo.class))).thenReturn(VeiculoResponse.builder().build());

		veiculoService.criar(req);
		verify(veiculoRepository).save(argThat(v -> "".equals(v.getPlaca())));
	}
}
