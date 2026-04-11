package br.app.veiculos.repository;

import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VinculoRepository extends JpaRepository<VeiculoPneu, Long> {

	@Query("""
			SELECT vp FROM VeiculoPneu vp
			JOIN FETCH vp.veiculo
			JOIN FETCH vp.pneu
			ORDER BY vp.id ASC
			""")
	List<VeiculoPneu> findAllFetchVeiculoEPneu();

	/**
	 * Uma consulta: {@link Veiculo}, toda a tabela {@code veiculo_pneu} (abertos e encerrados) e {@link br.app.veiculos.entity.Pneu}.
	 * O filtro “só aplicados” na API fica no {@link br.app.veiculos.service.VeiculoService}.
	 */
	@Query("""
			SELECT DISTINCT v FROM Veiculo v
			LEFT JOIN FETCH v.aplicacoes vp
			LEFT JOIN FETCH vp.pneu
			WHERE v.id = :veiculoId
			""")
	Optional<Veiculo> findVeiculoPneuPosicao(@Param("veiculoId") Long veiculoId);

	@Query("""
			SELECT vp FROM VeiculoPneu vp JOIN FETCH vp.veiculo
			WHERE vp.pneu.id = :pneuId AND vp.dataDesvinculo IS NULL
			""")
	List<VeiculoPneu> findAplicacoesAbertasPorPneuId(@Param("pneuId") Long pneuId, Pageable pageable);

	@Query("""
			SELECT COUNT(vp) FROM VeiculoPneu vp
			WHERE vp.veiculo.id = :veiculoId AND vp.posicao = :posicao AND vp.dataDesvinculo IS NULL
			""")
	long countAplicacoesAbertasNaPosicao(@Param("veiculoId") Long veiculoId, @Param("posicao") String posicao);

	@Query("""
			SELECT vp FROM VeiculoPneu vp JOIN FETCH vp.pneu
			WHERE vp.veiculo.id = :veiculoId AND vp.pneu.id = :pneuId AND vp.dataDesvinculo IS NULL
			""")
	List<VeiculoPneu> findAplicacoesAbertasPorVeiculoEPneu(@Param("veiculoId") Long veiculoId, @Param("pneuId") Long pneuId, Pageable pageable);

	default Optional<VeiculoPneu> findAplicacaoAbertaPorPneuId(Long pneuId) {
		List<VeiculoPneu> list = findAplicacoesAbertasPorPneuId(pneuId, PageRequest.of(0, 1));
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}

	default boolean existePneuAplicadoNaPosicao(Long veiculoId, String posicao) {
		return countAplicacoesAbertasNaPosicao(veiculoId, posicao) > 0;
	}

	default Optional<VeiculoPneu> findAplicacaoAbertaPorVeiculoEPneu(Long veiculoId, Long pneuId) {
		List<VeiculoPneu> list = findAplicacoesAbertasPorVeiculoEPneu(veiculoId, pneuId, PageRequest.of(0, 1));
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}
}
