package br.app.veiculos.mapper;

import br.app.veiculos.dto.PneuAplicadoResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.dto.VeiculoResponse;
import br.app.veiculos.entity.Pneu;
import br.app.veiculos.entity.Veiculo;
import br.app.veiculos.entity.VeiculoPneu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VeiculoMapper {

	public VeiculoResponse toVeiculoResponse(Veiculo v) {
		return VeiculoResponse.builder()
				.id(v.getId())
				.placa(v.getPlaca())
				.marca(v.getMarca())
				.quilometragemKm(v.getQuilometragemKm())
				.status(v.getStatus())
				.createdAt(v.getCreatedAt())
				.updatedAt(v.getUpdatedAt())
				.build();
	}

	public VeiculoDetalheResponse toDetalhe(Veiculo v, List<PneuAplicadoResponse> pneus) {
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

	public PneuAplicadoResponse toPneuAplicado(VeiculoPneu vp) {
		Pneu p = vp.getPneu();
		return PneuAplicadoResponse.builder()
				.pneuId(p.getId())
				.numeroDeFogo(p.getNumeroFogo())
				.posicao(vp.getPosicao())
				.build();
	}
}
