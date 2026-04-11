package br.app.veiculos.mapper;

import br.app.veiculos.dto.ListagemAplicacaoPneuResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ListagemAplicacaoPneuMapperTest {

	private final ListagemAplicacaoPneuMapper mapper = new ListagemAplicacaoPneuMapper();

	@Test
	void toResponse() {
		LocalDateTime now = LocalDateTime.now();
		Veiculo v = new Veiculo();
		v.setId(10L);
		Pneu p = new Pneu();
		p.setId(20L);
		VeiculoPneu vp = new VeiculoPneu();
		vp.setId(30L);
		vp.setVeiculo(v);
		vp.setPneu(p);
		vp.setPosicao("A");
		vp.setDataVinculo(now.minusDays(1));
		vp.setDataDesvinculo(null);
		vp.setCreatedAt(now);
		vp.setUpdatedAt(now);

		ListagemAplicacaoPneuResponse r = mapper.toResponse(vp);
		assertEquals(30L, r.getId());
		assertEquals(10L, r.getVeiculoId());
		assertEquals(20L, r.getPneuId());
		assertEquals("A", r.getPosicao());
		assertEquals(now.minusDays(1), r.getDataAplicacao());
		assertNull(r.getDataRemocao());
		assertEquals(now, r.getCreatedAt());
		assertEquals(now, r.getUpdatedAt());
	}

	@Test
	void toResponse_comDataRemocao() {
		Veiculo v = new Veiculo();
		v.setId(1L);
		Pneu p = new Pneu();
		p.setId(2L);
		LocalDateTime rem = LocalDateTime.now().minusHours(2);
		VeiculoPneu vp = new VeiculoPneu();
		vp.setId(3L);
		vp.setVeiculo(v);
		vp.setPneu(p);
		vp.setPosicao("B");
		vp.setDataVinculo(LocalDateTime.now().minusDays(2));
		vp.setDataDesvinculo(rem);
		vp.setCreatedAt(LocalDateTime.now().minusDays(2));
		vp.setUpdatedAt(LocalDateTime.now().minusDays(1));

		ListagemAplicacaoPneuResponse r = mapper.toResponse(vp);
		assertEquals(rem, r.getDataRemocao());
	}
}
