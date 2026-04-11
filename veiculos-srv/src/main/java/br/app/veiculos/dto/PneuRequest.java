package br.app.veiculos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PneuRequest {

	@NotBlank
	@Size(max = 50)
	private String numeroFogo;

	@NotBlank
	@Size(max = 100)
	private String marca;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal pressaoAtualPsi;

	@NotBlank
	@Size(max = 20)
	private String status;
}
