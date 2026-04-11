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
public class ListagemAplicacaoPneuResponse {

	private Long id;
	private Long veiculoId;
	private Long pneuId;
	private String posicao;
	private LocalDateTime dataAplicacao;
	private LocalDateTime dataRemocao;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
