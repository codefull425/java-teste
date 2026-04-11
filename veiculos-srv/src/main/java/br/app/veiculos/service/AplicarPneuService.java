package br.app.veiculos.service;

import br.app.veiculos.dto.AplicarPneuRequest;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.repository.PneuRepository;
import br.app.veiculos.repository.VeiculoRepository;
import br.app.veiculos.repository.VinculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AplicarPneuService {

	public static final String STATUS_PNEU_EM_USO = "EM_USO";

	private final VeiculoRepository veiculoRepository;
	private final PneuRepository pneuRepository;
	private final VinculoRepository vinculoRepository;

	@Transactional
	public void aplicar(Long veiculoId, AplicarPneuRequest request) {
		Veiculo veiculo = veiculoRepository.findById(veiculoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado"));
		Pneu pneu = pneuRepository.findById(request.getPneuId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pneu não encontrado"));

		String posicao = request.getPosicao().trim();

		Optional<VeiculoPneu> aplicacaoAtivaPneu = vinculoRepository.findAplicacaoAbertaPorPneuId(pneu.getId());
		if (aplicacaoAtivaPneu.isPresent()) {
			VeiculoPneu existente = aplicacaoAtivaPneu.get();
			if (existente.getVeiculo().getId().equals(veiculoId)) {
				throw new RegraNegocioException("Este pneu já está aplicado neste veículo.");
			}
			throw new RegraNegocioException("Este pneu já está aplicado em outro veículo.");
		}

		if (vinculoRepository.existePneuAplicadoNaPosicao(veiculoId, posicao)) {
			throw new RegraNegocioException("Já existe pneu aplicado nesta posição neste veículo.");
		}

		VeiculoPneu novo = new VeiculoPneu();
		novo.setVeiculo(veiculo);
		novo.setPneu(pneu);
		novo.setPosicao(posicao);
		novo.setDataVinculo(LocalDateTime.now());
		vinculoRepository.save(novo);

		pneu.setStatus(STATUS_PNEU_EM_USO);
		pneuRepository.save(pneu);
	}
}
