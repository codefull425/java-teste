package br.app.veiculos.service;

import br.app.veiculos.dto.*;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import br.app.veiculos.exception.RecursoNaoEncontradoException;
import br.app.veiculos.exception.RegraNegocioException;
import br.app.veiculos.repository.VeiculoPneuRepository;
import br.app.veiculos.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeiculoService {

	private final VeiculoRepository veiculoRepository;
	private final VeiculoPneuRepository veiculoPneuRepository;

	public List<VeiculoResumoResponse> listarSemPneus() {
		return veiculoRepository.findAll().stream()
				.map(this::toResumo)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public VeiculoDetalheResponse buscarComPneus(Long id) {
		Veiculo veiculo = veiculoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado"));
		List<VeiculoPneu> vinculos = veiculoPneuRepository.findAtivosComPneuByVeiculoId(id);
		List<PneuAplicadoResponse> pneus = vinculos.stream()
				.map(this::toPneuAplicado)
				.collect(Collectors.toList());
		return toDetalhe(veiculo, pneus);
	}

	@Transactional
	public VeiculoResumoResponse criar(VeiculoRequest request) {
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
		return toResumo(v);
	}

	private VeiculoResumoResponse toResumo(Veiculo v) {
		return VeiculoResumoResponse.builder()
				.id(v.getId())
				.placa(v.getPlaca())
				.marca(v.getMarca())
				.quilometragemKm(v.getQuilometragemKm())
				.status(v.getStatus())
				.createdAt(v.getCreatedAt())
				.updatedAt(v.getUpdatedAt())
				.build();
	}

	private VeiculoDetalheResponse toDetalhe(Veiculo v, List<PneuAplicadoResponse> pneus) {
		return VeiculoDetalheResponse.builder()
				.id(v.getId())
				.placa(v.getPlaca())
				.marca(v.getMarca())
				.quilometragemKm(v.getQuilometragemKm())
				.status(v.getStatus())
				.createdAt(v.getCreatedAt())
				.updatedAt(v.getUpdatedAt())
				.pneusAplicados(pneus)
				.build();
	}

	private PneuAplicadoResponse toPneuAplicado(VeiculoPneu vp) {
		Pneu p = vp.getPneu();
		return PneuAplicadoResponse.builder()
				.pneuId(p.getId())
				.numeroFogo(p.getNumeroFogo())
				.marca(p.getMarca())
				.pressaoAtualPsi(p.getPressaoAtualPsi())
				.status(p.getStatus())
				.posicao(vp.getPosicao())
				.dataVinculo(vp.getDataVinculo())
				.build();
	}

	private static String normalizarPlaca(String placa) {
		return placa == null ? "" : placa.trim().toUpperCase();
	}
}
