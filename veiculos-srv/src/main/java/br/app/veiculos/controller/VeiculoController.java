package br.app.veiculos.controller;

import br.app.veiculos.dto.*;
import br.app.veiculos.mapper.PneuMapper;
import br.app.veiculos.service.AplicarPneuService;
import br.app.veiculos.service.RemoverPneuService;
import br.app.veiculos.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VeiculoController {

	private final VeiculoService veiculoService;
	private final AplicarPneuService aplicarPneuService;
	private final RemoverPneuService removerPneuService;
	private final PneuMapper pneuMapper;

	@GetMapping
	public ResponseEntity<List<VeiculoResponse>> listar() {
		return ResponseEntity.ok(veiculoService.listarSemPneus());
	}

	@PostMapping
	public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest request) {
		VeiculoResponse body = veiculoService.criar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PostMapping("/{id}/aplicar-pneu")
	public ResponseEntity<AplicarPneuResponse> aplicarPneu(@PathVariable Long id, @Valid @RequestBody AplicarPneuRequest request) {
		aplicarPneuService.aplicar(id, request);
		AplicarPneuResponse body = AplicarPneuResponse.builder()
				.mensagem(AplicarPneuResponse.MENSAGEM_SUCESSO)
				.veiculo(veiculoService.buscarComPneus(id))
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PostMapping("/{id}/remover-pneu")
	public ResponseEntity<RemoverPneuResponse> removerPneu(@PathVariable Long id, @Valid @RequestBody RemoverPneuRequest request) {
		var pneu = removerPneuService.remover(id, request);
		RemoverPneuResponse body = RemoverPneuResponse.builder()
				.mensagem(RemoverPneuResponse.MENSAGEM_SUCESSO)
				.pneu(pneuMapper.toResponse(pneu))
				.build();
		return ResponseEntity.ok(body);
	}
}
