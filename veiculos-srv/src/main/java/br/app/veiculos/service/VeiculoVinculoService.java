package br.app.veiculos.service;

import br.app.veiculos.dto.DesvincularPneuRequest;
import br.app.veiculos.dto.VincularPneuRequest;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.repository.PneuRepository;
import br.app.veiculos.repository.VeiculoPneuRepository;
import br.app.veiculos.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VeiculoVinculoService {

	public static final String STATUS_PNEU_EM_USO = "EM_USO";
	public static final String STATUS_PNEU_DISPONIVEL = "DISPONIVEL";

	private final VeiculoRepository veiculoRepository;
	private final PneuRepository pneuRepository;
	private final VeiculoPneuRepository veiculoPneuRepository;

	@Transactional
	public void vincular(Long veiculoId, VincularPneuRequest request) {
		Veiculo veiculo = veiculoRepository.findById(veiculoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado"));
		Pneu pneu = pneuRepository.findById(request.getPneuId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pneu não encontrado"));

		String posicao = request.getPosicao().trim();
		if (posicao.isEmpty()) {
			throw new RegraNegocioException("Posição inválida.");
		}

		Optional<VeiculoPneu> vinculoAtivoPneu = veiculoPneuRepository.findAtivoByPneuId(pneu.getId());
		if (vinculoAtivoPneu.isPresent()) {
			VeiculoPneu existente = vinculoAtivoPneu.get();
			if (existente.getVeiculo().getId().equals(veiculoId)) {
				throw new RegraNegocioException("Este pneu já está vinculado a este veículo.");
			}
			throw new RegraNegocioException("Este pneu já está vinculado a outro veículo.");
		}

		if (veiculoPneuRepository.existsByVeiculo_IdAndPosicaoAndAtivoTrue(veiculoId, posicao)) {
			throw new RegraNegocioException("Já existe pneu ativo nesta posição neste veículo.");
		}

		VeiculoPneu novo = new VeiculoPneu();
		novo.setVeiculo(veiculo);
		novo.setPneu(pneu);
		novo.setPosicao(posicao);
		novo.setAtivo(true);
		novo.setDataVinculo(LocalDateTime.now());
		veiculoPneuRepository.save(novo);

		pneu.setStatus(STATUS_PNEU_EM_USO);
		pneuRepository.save(pneu);
	}

	@Transactional
	public void desvincular(Long veiculoId, DesvincularPneuRequest request) {
		if (!veiculoRepository.existsById(veiculoId)) {
			throw new RecursoNaoEncontradoException("Veículo não encontrado");
		}
		VeiculoPneu vinculo = veiculoPneuRepository
				.findByVeiculo_IdAndPneu_IdAndAtivoTrue(veiculoId, request.getPneuId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo ativo não encontrado para este veículo e pneu."));

		vinculo.setAtivo(false);
		vinculo.setDataDesvinculo(LocalDateTime.now());
		veiculoPneuRepository.save(vinculo);

		Pneu pneu = vinculo.getPneu();
		pneu.setStatus(STATUS_PNEU_DISPONIVEL);
		pneuRepository.save(pneu);
	}
}
