package br.app.veiculos.controller;

import br.app.veiculos.dto.ListagemAplicacaoPneuResponse;
import br.app.veiculos.dto.VeiculoDetalheResponse;
import br.app.veiculos.service.ListagemAplicacaoPneuQueryService;
import br.app.veiculos.service.VeiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculosPneus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ListagemAplicacaoPneuController {

	private final VeiculoService veiculoService;
	private final ListagemAplicacaoPneuQueryService listagemAplicacaoPneuQueryService;

	@GetMapping
	public ResponseEntity<List<ListagemAplicacaoPneuResponse>> listarTodos() {
		return ResponseEntity.ok(listagemAplicacaoPneuQueryService.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<VeiculoDetalheResponse> buscarVeiculoComPneus(@PathVariable Long id) {
		return ResponseEntity.ok(veiculoService.buscarComPneus(id));
	}
}
