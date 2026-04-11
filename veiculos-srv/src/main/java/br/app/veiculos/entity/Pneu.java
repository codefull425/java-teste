package br.app.veiculos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pneu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pneu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "numero_fogo", nullable = false, unique = true, length = 50)
	private String numeroFogo;

	@Column(nullable = false, length = 100)
	private String marca;

	@Column(name = "pressao_atual_psi", nullable = false, precision = 6, scale = 2)
	private BigDecimal pressaoAtualPsi;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

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
