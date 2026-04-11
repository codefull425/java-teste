package br.app.veiculos.service;

import br.app.veiculos.dto.RemoverPneuRequest;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.repository.PneuRepository;
import br.app.veiculos.repository.VeiculoRepository;
import br.app.veiculos.repository.VinculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RemoverPneuService {

	public static final String STATUS_PNEU_DISPONIVEL = "DISPONIVEL";

	private final VeiculoRepository veiculoRepository;
	private final PneuRepository pneuRepository;
	private final VinculoRepository vinculoRepository;

	@Transactional
	public Pneu remover(Long veiculoId, RemoverPneuRequest request) {
		if (!veiculoRepository.existsById(veiculoId)) {
			throw new RecursoNaoEncontradoException("Veículo não encontrado");
		}
		VeiculoPneu registro = vinculoRepository.findAplicacaoAbertaPorVeiculoEPneu(veiculoId, request.getPneuId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Não há aplicação em aberto deste pneu neste veículo."));

		registro.setDataDesvinculo(LocalDateTime.now());
		vinculoRepository.save(registro);

		Pneu pneu = registro.getPneu();
		pneu.setStatus(STATUS_PNEU_DISPONIVEL);
		return pneuRepository.save(pneu);
	}
}
