package br.app.veiculos.service;

import br.app.veiculos.dto.ListagemAplicacaoPneuResponse;
import br.app.veiculos.mapper.ListagemAplicacaoPneuMapper;
import br.app.veiculos.repository.VinculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListagemAplicacaoPneuQueryService {

	private final VinculoRepository vinculoRepository;
	private final ListagemAplicacaoPneuMapper listagemAplicacaoPneuMapper;

	@Transactional(readOnly = true)
	public List<ListagemAplicacaoPneuResponse> listarTodos() {
		return vinculoRepository.findAllFetchVeiculoEPneu().stream()
				.map(listagemAplicacaoPneuMapper::toResponse)
				.toList();
	}
}
