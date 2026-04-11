package br.app.veiculos.service;

import br.app.veiculos.dto.PneuRequest;
import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.mapper.PneuMapper;
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
	private final PneuMapper pneuMapper;
//Requisito 4 - lista todos os pneus
	@Transactional(readOnly = true)
	public List<PneuResponse> listar() {
		return pneuRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(pneuMapper::toResponse)
				.toList();
	}
//Requisito 5 - cria um pneu
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
		return pneuMapper.toResponse(p);
	}
}
