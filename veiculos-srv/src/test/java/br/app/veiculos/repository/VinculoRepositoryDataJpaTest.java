package br.app.veiculos.repository;

import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:jpa_veiculos;DB_CLOSE_DELAY=-1",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class VinculoRepositoryDataJpaTest {

	@Autowired
	private VeiculoRepository veiculoRepository;

	@Autowired
	private PneuRepository pneuRepository;

	@Autowired
	private VinculoRepository vinculoRepository;

	@Autowired
	private TestEntityManager em;

	private Veiculo veiculo;
	private Pneu pneu;

	@BeforeEach
	void setUp() {
		veiculo = new Veiculo();
		veiculo.setPlaca("ABC1D23");
		veiculo.setMarca("Volvo");
		veiculo.setQuilometragemKm(1000);
		veiculo.setStatus("ATIVO");
		em.persistAndFlush(veiculo);

		pneu = new Pneu();
		pneu.setNumeroFogo("NF1");
		pneu.setMarca("Michelin");
		pneu.setPressaoAtualPsi(new BigDecimal("100.00"));
		pneu.setStatus("DISPONIVEL");
		em.persistAndFlush(pneu);
	}

	@Test
	void veiculoRepository_existsByPlacaIgnoreCase() {
		assertTrue(veiculoRepository.existsByPlacaIgnoreCase("abc1d23"));
		assertFalse(veiculoRepository.existsByPlacaIgnoreCase("OUTRA"));
	}

	@Test
	void pneuRepository_existsByNumeroFogoIgnoreCase() {
		assertTrue(pneuRepository.existsByNumeroFogoIgnoreCase("nf1"));
		assertFalse(pneuRepository.existsByNumeroFogoIgnoreCase("X"));
	}

	@Test
	void findAllFetchVeiculoEPneu_acessaRelacoes() {
		VeiculoPneu vp = vinculoAberto("A");
		em.persistAndFlush(vp);

		List<VeiculoPneu> list = vinculoRepository.findAllFetchVeiculoEPneu();
		assertFalse(list.isEmpty());
		VeiculoPneu first = list.stream().filter(x -> x.getId().equals(vp.getId())).findFirst().orElseThrow();
		assertEquals("ABC1D23", first.getVeiculo().getPlaca());
		assertEquals("NF1", first.getPneu().getNumeroFogo());
	}

	@Test
	void findVeiculoPneuPosicao_veiculoInexistente() {
		assertTrue(vinculoRepository.findVeiculoPneuPosicao(9_999_999L).isEmpty());
	}

	@Test
	void findVeiculoPneuPosicao_carregaAplicacoesEPneus() {
		VeiculoPneu vp = vinculoAberto("B");
		veiculo.getAplicacoes().add(vp);
		em.persistAndFlush(vp);
		Long vid = veiculo.getId();
		em.clear();

		Optional<Veiculo> found = vinculoRepository.findVeiculoPneuPosicao(vid);
		assertTrue(found.isPresent());
		Veiculo v = found.get();
		assertEquals("ABC1D23", v.getPlaca());
		assertEquals(1, v.getAplicacoes().size());
		assertEquals("B", v.getAplicacoes().get(0).getPosicao());
		assertNotNull(v.getAplicacoes().get(0).getPneu());
	}

	@Test
	void findAplicacaoAbertaPorPneuId_quandoAberta() {
		em.persistAndFlush(vinculoAberto("C"));
		Optional<VeiculoPneu> opt = vinculoRepository.findAplicacaoAbertaPorPneuId(pneu.getId());
		assertTrue(opt.isPresent());
		assertEquals("C", opt.get().getPosicao());
	}

	@Test
	void findAplicacaoAbertaPorPneuId_semAplicacaoAberta() {
		Pneu solto = new Pneu();
		solto.setNumeroFogo("SOLTO");
		solto.setMarca("M");
		solto.setPressaoAtualPsi(new BigDecimal("90.00"));
		solto.setStatus("DISPONIVEL");
		em.persistAndFlush(solto);
		assertTrue(vinculoRepository.findAplicacaoAbertaPorPneuId(solto.getId()).isEmpty());
	}

	@Test
	void findAplicacaoAbertaPorVeiculoEPneu_semRegistro() {
		assertTrue(vinculoRepository.findAplicacaoAbertaPorVeiculoEPneu(veiculo.getId(), 9_999_999L).isEmpty());
	}

	@Test
	void findAplicacaoAbertaPorPneuId_quandoEncerrada() {
		VeiculoPneu vp = vinculoAberto("D");
		vp.setDataDesvinculo(LocalDateTime.now());
		em.persistAndFlush(vp);

		assertTrue(vinculoRepository.findAplicacaoAbertaPorPneuId(pneu.getId()).isEmpty());
	}

	@Test
	void existePneuAplicadoNaPosicao() {
		em.persistAndFlush(vinculoAberto("E"));
		assertTrue(vinculoRepository.existePneuAplicadoNaPosicao(veiculo.getId(), "E"));
		assertFalse(vinculoRepository.existePneuAplicadoNaPosicao(veiculo.getId(), "Z"));
	}

	@Test
	void findAplicacaoAbertaPorVeiculoEPneu() {
		em.persistAndFlush(vinculoAberto("F"));
		Optional<VeiculoPneu> opt = vinculoRepository.findAplicacaoAbertaPorVeiculoEPneu(veiculo.getId(), pneu.getId());
		assertTrue(opt.isPresent());
		assertEquals("F", opt.get().getPosicao());
	}

	@Test
	void veiculoPneu_prePersist_dataVinculoNull_usaAgora() {
		VeiculoPneu vp = new VeiculoPneu();
		vp.setVeiculo(veiculo);
		vp.setPneu(pneu);
		vp.setPosicao("G");
		vp.setDataVinculo(null);
		em.persistAndFlush(vp);
		assertNotNull(vp.getDataVinculo());
	}

	@Test
	void veiculoPneu_prePersist_dataVinculoPreenchido_preserva() {
		LocalDateTime fixo = LocalDateTime.now().minusDays(5);
		VeiculoPneu vp = new VeiculoPneu();
		vp.setVeiculo(veiculo);
		vp.setPneu(pneu);
		vp.setPosicao("H");
		vp.setDataVinculo(fixo);
		em.persistAndFlush(vp);
		assertEquals(fixo, vp.getDataVinculo());
	}

	@Test
	void veiculoPneu_preUpdate_atualizaUpdatedAt() {
		VeiculoPneu vp = vinculoAberto("I");
		em.persistAndFlush(vp);
		LocalDateTime antes = vp.getUpdatedAt();
		vp.setPosicao("I2");
		em.merge(vp);
		em.flush();
		assertNotNull(vp.getUpdatedAt());
		assertTrue(!vp.getUpdatedAt().isBefore(antes));
	}

	@Test
	void veiculo_prePersist_e_preUpdate() {
		Veiculo v = new Veiculo();
		v.setPlaca("ZZZ9Z99");
		v.setMarca("M");
		v.setQuilometragemKm(1);
		v.setStatus("ATIVO");
		em.persistAndFlush(v);
		assertNotNull(v.getCreatedAt());
		LocalDateTime antes = v.getUpdatedAt();
		v.setMarca("M2");
		em.merge(v);
		em.flush();
		assertNotNull(v.getUpdatedAt());
		assertTrue(!v.getUpdatedAt().isBefore(antes));
	}

	@Test
	void pneu_prePersist_e_preUpdate() {
		Pneu p = new Pneu();
		p.setNumeroFogo("NF2");
		p.setMarca("X");
		p.setPressaoAtualPsi(new BigDecimal("99.00"));
		p.setStatus("DISPONIVEL");
		em.persistAndFlush(p);
		assertNotNull(p.getCreatedAt());
		LocalDateTime antes = p.getUpdatedAt();
		p.setMarca("Y");
		em.merge(p);
		em.flush();
		assertTrue(!p.getUpdatedAt().isBefore(antes));
	}

	private VeiculoPneu vinculoAberto(String posicao) {
		VeiculoPneu vp = new VeiculoPneu();
		vp.setVeiculo(veiculo);
		vp.setPneu(pneu);
		vp.setPosicao(posicao);
		vp.setDataVinculo(LocalDateTime.now().minusHours(1));
		return vp;
	}
}
