package br.app.veiculos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "veiculo_pneu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoPneu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "veiculo_id", nullable = false)
	private Veiculo veiculo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pneu_id", nullable = false)
	private Pneu pneu;

	@Column(nullable = false, length = 50)
	private String posicao;

	@Column(name = "data_vinculo", nullable = false)
	private LocalDateTime dataVinculo;

	@Column(name = "data_desvinculo")
	private LocalDateTime dataDesvinculo;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (dataVinculo == null) {
			dataVinculo = now;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
