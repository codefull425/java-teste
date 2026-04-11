package br.app.veiculos.repository;

import br.app.veiculos.entity.VeiculoPneu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VeiculoPneuRepository extends JpaRepository<VeiculoPneu, Long> {

	@Query("SELECT vp FROM VeiculoPneu vp JOIN FETCH vp.pneu JOIN FETCH vp.veiculo WHERE vp.pneu.id = :pneuId AND vp.ativo = true")
	Optional<VeiculoPneu> findAtivoByPneuId(@Param("pneuId") Long pneuId);

	@Query("SELECT vp FROM VeiculoPneu vp JOIN FETCH vp.pneu WHERE vp.veiculo.id = :veiculoId AND vp.ativo = true")
	List<VeiculoPneu> findAtivosComPneuByVeiculoId(@Param("veiculoId") Long veiculoId);

	Optional<VeiculoPneu> findByVeiculo_IdAndPneu_IdAndAtivoTrue(Long veiculoId, Long pneuId);

	boolean existsByVeiculo_IdAndPosicaoAndAtivoTrue(Long veiculoId, String posicao);
}
