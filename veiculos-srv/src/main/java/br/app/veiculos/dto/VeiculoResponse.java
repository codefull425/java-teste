package br.app.veiculos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoResponse {

	private Long id;
	private String placa;
	private String marca;
	private Integer quilometragemKm;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
