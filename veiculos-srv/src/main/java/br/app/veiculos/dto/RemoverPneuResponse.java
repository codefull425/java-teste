package br.app.veiculos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoverPneuResponse {

	public static final String MENSAGEM_SUCESSO = "PNEU REMOVIDO COM SUCESSO";

	private String mensagem;
	private PneuResponse pneu;
}
