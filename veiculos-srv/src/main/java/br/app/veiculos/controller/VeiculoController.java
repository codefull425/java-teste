package br.app.veiculos.controller;

import br.app.veiculos.dto.*;
import br.app.veiculos.service.VeiculoService;
import br.app.veiculos.service.VeiculoVinculoService;
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
	private final VeiculoVinculoService veiculoVinculoService;

	@GetMapping
	public ResponseEntity<List<VeiculoResumoResponse>> listar() {
		return ResponseEntity.ok(veiculoService.listarSemPneus());
	}

	@GetMapping("/{id}")
	public ResponseEntity<VeiculoDetalheResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(veiculoService.buscarComPneus(id));
	}

	@PostMapping
	public ResponseEntity<VeiculoResumoResponse> criar(@Valid @RequestBody VeiculoRequest request) {
		VeiculoResumoResponse body = veiculoService.criar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PostMapping("/{id}/vinculos")
	public ResponseEntity<Void> vincularPneu(@PathVariable Long id, @Valid @RequestBody VincularPneuRequest request) {
		veiculoVinculoService.vincular(id, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/{id}/desvinculos")
	public ResponseEntity<Void> desvincularPneu(@PathVariable Long id, @Valid @RequestBody DesvincularPneuRequest request) {
		veiculoVinculoService.desvincular(id, request);
		return ResponseEntity.noContent().build();
	}
}
