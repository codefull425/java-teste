package br.app.veiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AplicarPneuRequest {

	@NotNull
	private Long pneuId;

	@NotBlank
	@Size(max = 50)
	private String posicao;
}
