package br.app.veiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veiculo")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String placa;

	@Column(nullable = false, length = 100)
	private String marca;

	@Column(name = "quilometragem_km", nullable = false)
	private Integer quilometragemKm;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	/** Listagem de aplicações ({@code veiculo_pneu}); montagem atual quando {@link VeiculoPneu#getDataDesvinculo()} é {@code null}. */
	@OneToMany(mappedBy = "veiculo", fetch = FetchType.LAZY)
	private List<VeiculoPneu> aplicacoes = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
