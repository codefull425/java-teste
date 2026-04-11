-- =============================================================================
-- Seed: dados iniciais para desenvolvimento e demonstração do teste prático.
-- Ordem dos primeiros veículos/pneus preserva ids esperados pelos testes (id 1 = ABC1D23, pneus 1..5).
-- =============================================================================

-- Veículos (ids 1–3 usados nos testes de integração)
INSERT INTO veiculo (placa, marca, quilometragem_km, status) VALUES
    ('ABC1D23', 'Volvo', 125000, 'ATIVO'),
    ('XYZ9K87', 'Scania', 89000, 'ATIVO'),
    ('TRK1880', 'Mercedes-Benz', 210000, 'INATIVO');

-- Veículos adicionais
INSERT INTO veiculo (placa, marca, quilometragem_km, status) VALUES
    ('RIO2A44', 'Iveco', 45200, 'ATIVO'),
    ('SPB9F11', 'MAN', 178300, 'ATIVO'),
    ('CUR3B77', 'DAF', 99000, 'INATIVO');

-- Pneus base (ids 1–5 alinhados aos testes: pneu 3 = número 190 disponível)
INSERT INTO pneu (numero_fogo, marca, pressao_atual_psi, status) VALUES
    ('188', 'Michelin', 100.00, 'EM_USO'),
    ('189', 'Bridgestone', 98.50, 'EM_USO'),
    ('190', 'Goodyear', 95.00, 'DISPONIVEL'),
    ('191', 'Pirelli', 99.00, 'DISPONIVEL'),
    ('192', 'Continental', 97.00, 'EM_USO');

-- Pneus adicionais (status ajustado ao final conforme vínculos ativos)
INSERT INTO pneu (numero_fogo, marca, pressao_atual_psi, status) VALUES
    ('193', 'Michelin', 102.00, 'EM_USO'),
    ('194', 'Bridgestone', 96.00, 'EM_USO'),
    ('195', 'Goodyear', 98.00, 'EM_USO'),
    ('196', 'Pirelli', 101.00, 'DISPONIVEL'),
    ('197', 'Continental', 94.50, 'DISPONIVEL'),
    ('198', 'Michelin', 99.50, 'EM_USO'),
    ('199', 'Bridgestone', 97.50, 'DISPONIVEL');

-- -----------------------------------------------------------------------------
-- Vínculos atuais e histórico (referências por placa / número de fogo)
-- -----------------------------------------------------------------------------

-- Truck ABC1D23: pneu 188 na posição A (exemplo do enunciado) + B
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'A', TRUE, CURRENT_TIMESTAMP - INTERVAL '30 days'
FROM veiculo v, pneu p
WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '188';

INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'B', TRUE, CURRENT_TIMESTAMP - INTERVAL '25 days'
FROM veiculo v, pneu p
WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '189';

-- Scania XYZ9K87: um pneu ativo
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'DIANTEIRO_ESQUERDO', TRUE, CURRENT_TIMESTAMP - INTERVAL '10 days'
FROM veiculo v, pneu p
WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = '192';

-- Histórico: pneu 191 esteve no veículo XYZ9K87 e foi desmontado
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo, data_desvinculo)
SELECT v.id, p.id, 'DIANTEIRO_DIREITO', FALSE, CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '15 days'
FROM veiculo v, pneu p
WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = '191';

-- Iveco RIO2A44: bitrem simplificado (4 posições)
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'E1_DIANTEIRO_ESQ', TRUE, CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM veiculo v, pneu p
WHERE v.placa = 'RIO2A44' AND p.numero_fogo = '195';

INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'E1_DIANTEIRO_DIR', TRUE, CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM veiculo v, pneu p
WHERE v.placa = 'RIO2A44' AND p.numero_fogo = '198';

INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'E2_TRASEIRO_ESQ', TRUE, CURRENT_TIMESTAMP - INTERVAL '4 days'
FROM veiculo v, pneu p
WHERE v.placa = 'RIO2A44' AND p.numero_fogo = '193';

INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo)
SELECT v.id, p.id, 'E2_TRASEIRO_DIR', TRUE, CURRENT_TIMESTAMP - INTERVAL '4 days'
FROM veiculo v, pneu p
WHERE v.placa = 'RIO2A44' AND p.numero_fogo = '194';

-- Histórico: pneu 193 rodou antes em outro veículo (ABC1D23), depois remontado no Iveco — registro antigo inativo
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo, data_desvinculo)
SELECT v.id, p.id, 'C', FALSE, CURRENT_TIMESTAMP - INTERVAL '400 days', CURRENT_TIMESTAMP - INTERVAL '200 days'
FROM veiculo v, pneu p
WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '193';

-- Veículo inativo TRK1880: sem vínculos ativos (apenas histórico)
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, ativo, data_vinculo, data_desvinculo)
SELECT v.id, p.id, 'A', FALSE, CURRENT_TIMESTAMP - INTERVAL '500 days', CURRENT_TIMESTAMP - INTERVAL '90 days'
FROM veiculo v, pneu p
WHERE v.placa = 'TRK1880' AND p.numero_fogo = '196';
