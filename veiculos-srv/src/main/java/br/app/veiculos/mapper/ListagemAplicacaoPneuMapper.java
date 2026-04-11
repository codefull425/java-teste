package br.app.veiculos.mapper;

import br.app.veiculos.dto.ListagemAplicacaoPneuResponse;
import br.app.veiculos.entity.VeiculoPneu;
import org.springframework.stereotype.Component;

@Component
public class ListagemAplicacaoPneuMapper {

	public ListagemAplicacaoPneuResponse toResponse(VeiculoPneu vp) {
		return ListagemAplicacaoPneuResponse.builder()
				.id(vp.getId())
				.veiculoId(vp.getVeiculo().getId())
				.pneuId(vp.getPneu().getId())
				.posicao(vp.getPosicao())
				.dataAplicacao(vp.getDataVinculo())
				.dataRemocao(vp.getDataDesvinculo())
				.createdAt(vp.getCreatedAt())
				.updatedAt(vp.getUpdatedAt())
				.build();
	}
}
