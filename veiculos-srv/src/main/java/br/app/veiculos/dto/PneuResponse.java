package br.app.veiculos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PneuResponse {

	private Long id;
	private String numeroFogo;
	private String marca;
	private BigDecimal pressaoAtualPsi;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
