package br.app.veiculos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VeiculoRequest {

	@NotBlank
	@Size(max = 20)
	private String placa;

	@NotBlank
	@Size(max = 100)
	private String marca;

	@NotNull
	@Min(0)
	private Integer quilometragemKm;

	@NotBlank
	@Size(max = 20)
	private String status;
}
