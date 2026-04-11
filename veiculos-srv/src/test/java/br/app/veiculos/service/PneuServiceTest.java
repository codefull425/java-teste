package br.app.veiculos.service;

import br.app.veiculos.dto.PneuRequest;
import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.mapper.PneuMapper;
import br.app.veiculos.repository.PneuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PneuServiceTest {

	@Mock
	private PneuRepository pneuRepository;

	@Mock
	private PneuMapper pneuMapper;

	@InjectMocks
	private PneuService pneuService;

	@Test
	void listar() {
		Pneu p = new Pneu();
		p.setId(1L);
		when(pneuRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(p));
		when(pneuMapper.toResponse(p)).thenReturn(PneuResponse.builder().id(1L).build());

		assertEquals(1, pneuService.listar().size());
		verify(pneuRepository).findAll(Sort.by(Sort.Direction.ASC, "id"));
	}

	@Test
	void criar_sucesso() {
		PneuRequest req = new PneuRequest();
		req.setNumeroFogo("  nf1 ");
		req.setMarca("  Michelin ");
		req.setPressaoAtualPsi(new BigDecimal("100.5"));
		req.setStatus(" disponivel ");

		when(pneuRepository.existsByNumeroFogoIgnoreCase("nf1")).thenReturn(false);
		when(pneuRepository.save(any(Pneu.class))).thenAnswer(inv -> {
			Pneu x = inv.getArgument(0);
			x.setId(3L);
			return x;
		});
		when(pneuMapper.toResponse(any(Pneu.class))).thenReturn(PneuResponse.builder().id(3L).numeroFogo("nf1").build());

		PneuResponse res = pneuService.criar(req);
		assertEquals(3L, res.getId());
		verify(pneuRepository).save(argThat(pneu -> "nf1".equals(pneu.getNumeroFogo()) && "Michelin".equals(pneu.getMarca()) && "DISPONIVEL".equals(pneu.getStatus())));
	}

	@Test
	void criar_numeroFogoDuplicado() {
		PneuRequest req = new PneuRequest();
		req.setNumeroFogo("x");
		req.setMarca("m");
		req.setPressaoAtualPsi(BigDecimal.ONE);
		req.setStatus("ATIVO");
		when(pneuRepository.existsByNumeroFogoIgnoreCase("x")).thenReturn(true);
		assertThrows(RegraNegocioException.class, () -> pneuService.criar(req));
		verify(pneuRepository, never()).save(any());
	}
}
