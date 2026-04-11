package br.app.veiculos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicarPneuResponse {

	public static final String MENSAGEM_SUCESSO = "PNEU APLICADO COM SUCESSO";

	private String mensagem;
	private VeiculoDetalheResponse veiculo;
}
