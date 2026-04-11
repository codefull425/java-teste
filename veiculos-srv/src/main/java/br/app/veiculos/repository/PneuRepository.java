package br.app.veiculos.repository;

import br.app.veiculos.entity.Pneu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PneuRepository extends JpaRepository<Pneu, Long> {

	Optional<Pneu> findByNumeroFogoIgnoreCase(String numeroFogo);

	boolean existsByNumeroFogoIgnoreCase(String numeroFogo);
}
