package br.app.veiculos.repository;

import br.app.veiculos.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

	Optional<Veiculo> findByPlacaIgnoreCase(String placa);

	boolean existsByPlacaIgnoreCase(String placa);
}
