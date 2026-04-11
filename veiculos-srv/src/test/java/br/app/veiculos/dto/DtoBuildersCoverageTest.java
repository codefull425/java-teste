package br.app.veiculos.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Exercita builders/getters Lombok usados na API e em respostas JSON.
 */
class DtoBuildersCoverageTest {

	@Test
	void aplicarPneuResponse() {
		VeiculoDetalheResponse v = VeiculoDetalheResponse.builder().id(1L).placa("P").build();
		AplicarPneuResponse r = AplicarPneuResponse.builder().mensagem(AplicarPneuResponse.MENSAGEM_SUCESSO).veiculo(v).build();
		assertEquals(AplicarPneuResponse.MENSAGEM_SUCESSO, r.getMensagem());
		assertEquals("P", r.getVeiculo().getPlaca());
	}

	@Test
	void removerPneuResponse() {
		PneuResponse p = PneuResponse.builder().id(1L).numeroFogo("188").build();
		RemoverPneuResponse r = RemoverPneuResponse.builder().mensagem(RemoverPneuResponse.MENSAGEM_SUCESSO).pneu(p).build();
		assertEquals(RemoverPneuResponse.MENSAGEM_SUCESSO, r.getMensagem());
		assertEquals("188", r.getPneu().getNumeroFogo());
	}

	@Test
	void veiculoDetalheResponse() {
		VeiculoDetalheResponse d = VeiculoDetalheResponse.builder()
				.id(1L).placa("X").marca("M").quilometragemKm(1).status("S")
				.createdAt(LocalDateTime.MIN).updatedAt(LocalDateTime.MAX)
				.pneusAplicados(List.of(PneuAplicadoResponse.builder().pneuId(2L).numeroDeFogo("n").posicao("A").build()))
				.build();
		assertEquals(1, d.getPneusAplicados().size());
		assertEquals("A", d.getPneusAplicados().get(0).getPosicao());
	}

	@Test
	void veiculoResponse() {
		VeiculoResponse r = VeiculoResponse.builder().id(3L).placa("Z").marca("Ma").quilometragemKm(9).status("ATIVO")
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		assertEquals(3L, r.getId());
	}

	@Test
	void pneuResponse() {
		PneuResponse r = PneuResponse.builder().id(1L).numeroFogo("1").marca("m").pressaoAtualPsi(BigDecimal.TEN).status("S")
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		assertNotNull(r.getPressaoAtualPsi());
	}

	@Test
	void listagemAplicacaoPneuResponse() {
		ListagemAplicacaoPneuResponse r = ListagemAplicacaoPneuResponse.builder()
				.id(1L).veiculoId(2L).pneuId(3L).posicao("P")
				.dataAplicacao(LocalDateTime.now()).dataRemocao(LocalDateTime.now().plusHours(1))
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		assertNotNull(r.getDataRemocao());
	}

	@Test
	void requests() {
		AplicarPneuRequest a = new AplicarPneuRequest();
		a.setPneuId(1L);
		a.setPosicao("A");
		assertEquals(1L, a.getPneuId());

		RemoverPneuRequest b = new RemoverPneuRequest();
		b.setPneuId(2L);
		assertEquals(2L, b.getPneuId());

		PneuRequest c = new PneuRequest();
		c.setNumeroFogo("n");
		c.setMarca("m");
		c.setPressaoAtualPsi(BigDecimal.ONE);
		c.setStatus("S");
		assertEquals("n", c.getNumeroFogo());

		VeiculoRequest d = new VeiculoRequest();
		d.setPlaca("p");
		d.setMarca("m");
		d.setQuilometragemKm(0);
		d.setStatus("ATIVO");
		assertEquals("p", d.getPlaca());
	}
}
