package br.app.veiculos.service;

import br.app.veiculos.dto.PneuAplicadoResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.dto.VeiculoRequest;
import br.app.veiculos.dto.VeiculoResponse;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.mapper.VeiculoMapper;
import br.app.veiculos.repository.VeiculoRepository;
import br.app.veiculos.repository.VinculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeiculoService {

	private final VeiculoRepository veiculoRepository;
	private final VinculoRepository vinculoRepository;
	private final VeiculoMapper veiculoMapper;
//Requisito 1 - lista todos os veiculos sem pneus
	public List<VeiculoResponse> listarSemPneus() {
		return veiculoRepository.findAll().stream()
				.map(veiculoMapper::toVeiculoResponse)
				.collect(Collectors.toList());
	}
//Requisito 2 - busca um veiculo com pneu e posição
	@Transactional(readOnly = true)
	public VeiculoDetalheResponse buscarComPneus(Long id) {
		Veiculo veiculo = vinculoRepository.findVeiculoPneuPosicao(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado"));
		List<PneuAplicadoResponse> pneus = veiculo.getAplicacoes().stream()
				.filter(vp -> vp.getDataDesvinculo() == null)
				.map(veiculoMapper::toPneuAplicado)
				.toList();
		return veiculoMapper.toDetalhe(veiculo, pneus);
	}
//Requisito 3 - cria um veiculo
	@Transactional
	public VeiculoResponse criar(VeiculoRequest request) {
		String placa = normalizarPlaca(request.getPlaca());
		if (veiculoRepository.existsByPlacaIgnoreCase(placa)) {
			throw new RegraNegocioException("Já existe veículo com esta placa.");
		}
		Veiculo v = new Veiculo();
		v.setPlaca(placa);
		v.setMarca(request.getMarca().trim());
		v.setQuilometragemKm(request.getQuilometragemKm());
		v.setStatus(request.getStatus().trim().toUpperCase());
		v = veiculoRepository.save(v);
		return veiculoMapper.toVeiculoResponse(v);
	}

	private static String normalizarPlaca(String placa) {
		return placa == null ? "" : placa.trim().toUpperCase();
	}
}
