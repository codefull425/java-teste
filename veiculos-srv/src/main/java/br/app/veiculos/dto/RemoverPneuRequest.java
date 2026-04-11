package br.app.veiculos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoverPneuRequest {

	@NotNull
	private Long pneuId;
}
