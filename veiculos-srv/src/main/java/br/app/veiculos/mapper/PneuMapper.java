package br.app.veiculos.mapper;

import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.entity.Pneu;
import org.springframework.stereotype.Component;

@Component
public class PneuMapper {

	public PneuResponse toResponse(Pneu p) {
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
