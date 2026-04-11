package br.app.veiculos.mapper;

import br.app.veiculos.dto.PneuAplicadoResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.dto.VeiculoResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VeiculoMapperTest {

	private final VeiculoMapper mapper = new VeiculoMapper();

	@Test
	void toVeiculoResponse() {
		LocalDateTime now = LocalDateTime.now();
		Veiculo v = new Veiculo();
		v.setId(2L);
		v.setPlaca("ABC1D23");
		v.setMarca("Volvo");
		v.setQuilometragemKm(100);
		v.setStatus("ATIVO");
		v.setCreatedAt(now);
		v.setUpdatedAt(now);

		VeiculoResponse r = mapper.toVeiculoResponse(v);
		assertEquals(2L, r.getId());
		assertEquals("ABC1D23", r.getPlaca());
		assertEquals("Volvo", r.getMarca());
		assertEquals(100, r.getQuilometragemKm());
		assertEquals("ATIVO", r.getStatus());
		assertEquals(now, r.getCreatedAt());
		assertEquals(now, r.getUpdatedAt());
	}

	@Test
	void toDetalhe() {
		LocalDateTime now = LocalDateTime.now();
		Veiculo v = new Veiculo();
		v.setId(1L);
		v.setPlaca("X");
		v.setMarca("M");
		v.setQuilometragemKm(1);
		v.setStatus("S");
		v.setCreatedAt(now);
		v.setUpdatedAt(now);
		List<PneuAplicadoResponse> pneus = List.of(PneuAplicadoResponse.builder().pneuId(9L).build());

		VeiculoDetalheResponse d = mapper.toDetalhe(v, pneus);
		assertEquals(1L, d.getId());
		assertEquals("X", d.getPlaca());
		assertEquals(1, d.getPneusAplicados().size());
		assertEquals(9L, d.getPneusAplicados().get(0).getPneuId());
	}

	@Test
	void toPneuAplicado() {
		Pneu p = new Pneu();
		p.setId(7L);
		p.setNumeroFogo("999");
		VeiculoPneu vp = new VeiculoPneu();
		vp.setPneu(p);
		vp.setPosicao("D");

		PneuAplicadoResponse r = mapper.toPneuAplicado(vp);
		assertEquals(7L, r.getPneuId());
		assertEquals("999", r.getNumeroDeFogo());
		assertEquals("D", r.getPosicao());
	}
}
