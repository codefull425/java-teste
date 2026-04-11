package br.app.veiculos.service;

import br.app.veiculos.dto.PneuRequest;
import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.repository.PneuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PneuService {

	private final PneuRepository pneuRepository;

	@Transactional(readOnly = true)
	public List<PneuResponse> listar() {
		return pneuRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public PneuResponse criar(PneuRequest request) {
		String numero = request.getNumeroFogo().trim();
		if (pneuRepository.existsByNumeroFogoIgnoreCase(numero)) {
			throw new RegraNegocioException("Já existe pneu com este número de fogo.");
		}
		Pneu p = new Pneu();
		p.setNumeroFogo(numero);
		p.setMarca(request.getMarca().trim());
		p.setPressaoAtualPsi(request.getPressaoAtualPsi());
		p.setStatus(request.getStatus().trim().toUpperCase());
		p = pneuRepository.save(p);
		return toResponse(p);
	}

	private PneuResponse toResponse(Pneu p) {
		return PneuResponse.builder()
				.id(p.getId())
				.numeroFogo(p.getNumeroFogo())
				.marca(p.getMarca())
				.pressaoAtualPsi(p.getPressaoAtualPsi())
				.status(p.getStatus())
				.createdAt(p.getCreatedAt())
				.updatedAt(p.getUpdatedAt())
				.build();
	}
}
