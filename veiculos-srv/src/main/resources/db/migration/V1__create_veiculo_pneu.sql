-- Modelo: VEICULO, PNEU e veiculo_pneu com listagem de períodos. Aplicação em aberto = data_desvinculo IS NULL.

CREATE TABLE veiculo (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    quilometragem_km INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_veiculo_placa UNIQUE (placa)
);

CREATE TABLE pneu (
    id BIGSERIAL PRIMARY KEY,
    numero_fogo VARCHAR(50) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    pressao_atual_psi NUMERIC(6, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_pneu_numero_fogo UNIQUE (numero_fogo)
);

CREATE TABLE veiculo_pneu (
    id BIGSERIAL PRIMARY KEY,
    veiculo_id BIGINT NOT NULL REFERENCES veiculo (id),
    pneu_id BIGINT NOT NULL REFERENCES pneu (id),
    posicao VARCHAR(50) NOT NULL,
    data_vinculo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_desvinculo TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_veiculo_pneu_veiculo ON veiculo_pneu (veiculo_id);
CREATE INDEX idx_veiculo_pneu_pneu ON veiculo_pneu (pneu_id);
CREATE INDEX idx_veiculo_pneu_veiculo_aberto ON veiculo_pneu (veiculo_id) WHERE data_desvinculo IS NULL;

-- Um veículo não pode ter dois vínculos abertos na mesma posição.
CREATE UNIQUE INDEX uk_veiculo_posicao_aberta ON veiculo_pneu (veiculo_id, posicao) WHERE data_desvinculo IS NULL;

-- Um pneu não pode ter vínculo aberto em mais de um veículo ao mesmo tempo.
CREATE UNIQUE INDEX uk_pneu_vinculo_aberto ON veiculo_pneu (pneu_id) WHERE data_desvinculo IS NULL;
