package br.app.veiculos.controller;

import br.app.veiculos.dto.PneuRequest;
import br.app.veiculos.dto.PneuResponse;
import br.app.veiculos.service.PneuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PneuController {

	private final PneuService pneuService;

	@GetMapping
	public ResponseEntity<List<PneuResponse>> listar() {
		return ResponseEntity.ok(pneuService.listar());
	}

	@PostMapping
	public ResponseEntity<PneuResponse> criar(@Valid @RequestBody PneuRequest request) {
		PneuResponse body = pneuService.criar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
}
