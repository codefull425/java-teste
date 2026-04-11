package br.app.veiculos.mapper;

import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.entity.Pneu;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PneuMapperTest {

	private final PneuMapper mapper = new PneuMapper();

	@Test
	void toResponse() {
		LocalDateTime now = LocalDateTime.now();
		Pneu p = new Pneu();
		p.setId(1L);
		p.setNumeroFogo("188");
		p.setMarca("Michelin");
		p.setPressaoAtualPsi(new BigDecimal("100.00"));
		p.setStatus("EM_USO");
		p.setCreatedAt(now);
		p.setUpdatedAt(now);

		PneuResponse r = mapper.toResponse(p);
		assertEquals(1L, r.getId());
		assertEquals("188", r.getNumeroFogo());
		assertEquals("Michelin", r.getMarca());
		assertEquals(new BigDecimal("100.00"), r.getPressaoAtualPsi());
		assertEquals("EM_USO", r.getStatus());
		assertEquals(now, r.getCreatedAt());
		assertEquals(now, r.getUpdatedAt());
	}
}
