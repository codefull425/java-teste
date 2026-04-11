package br.app.veiculos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DesvincularPneuRequest {

	@NotNull
	private Long pneuId;
}
