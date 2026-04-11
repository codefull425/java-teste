-- =============================================================================
-- Seed: cada veículo com pelo menos 6 pneus diferentes aplicados em aberto (data_desvinculo nula).
-- Preserva testes: ids pneu 1–5 (190 = id 3 DISPONIVEL); veículo 1 ABC1D23
-- com posições A,B,D,E,F,G em aberto (C livre para POST aplicar-pneu em C).
-- =============================================================================

INSERT INTO veiculo (placa, marca, quilometragem_km, status) VALUES
    ('ABC1D23', 'Volvo', 125000, 'ATIVO'),
    ('XYZ9K87', 'Scania', 89000, 'ATIVO'),
    ('TRK1880', 'Mercedes-Benz', 210000, 'INATIVO'),
    ('RIO2A44', 'Iveco', 45200, 'ATIVO'),
    ('SPB9F11', 'MAN', 178300, 'ATIVO'),
    ('CUR3B77', 'DAF', 99000, 'INATIVO');

-- Pneus 1–5 (ids fixos para testes de integração)
INSERT INTO pneu (numero_fogo, marca, pressao_atual_psi, status) VALUES
    ('188', 'Michelin', 100.00, 'EM_USO'),
    ('189', 'Bridgestone', 98.50, 'EM_USO'),
    ('190', 'Goodyear', 95.00, 'DISPONIVEL'),
    ('191', 'Pirelli', 99.00, 'EM_USO'),
    ('192', 'Continental', 97.00, 'EM_USO');

-- Pneus adicionais (T201–T237): pool para 6×6 montagens em aberto + folga; status EM_USO exceto 190 acima
INSERT INTO pneu (numero_fogo, marca, pressao_atual_psi, status) VALUES
    ('T201', 'Michelin', 100.00, 'EM_USO'),
    ('T202', 'Michelin', 101.00, 'EM_USO'),
    ('T203', 'Bridgestone', 99.00, 'EM_USO'),
    ('T204', 'Bridgestone', 98.00, 'EM_USO'),
    ('T205', 'Goodyear', 97.00, 'EM_USO'),
    ('T206', 'Goodyear', 96.00, 'EM_USO'),
    ('T207', 'Pirelli', 102.00, 'EM_USO'),
    ('T208', 'Pirelli', 101.00, 'EM_USO'),
    ('T209', 'Continental', 100.00, 'EM_USO'),
    ('T210', 'Continental', 99.50, 'EM_USO'),
    ('T211', 'Michelin', 100.50, 'EM_USO'),
    ('T212', 'Bridgestone', 98.50, 'EM_USO'),
    ('T213', 'Goodyear', 97.50, 'EM_USO'),
    ('T214', 'Pirelli', 101.50, 'EM_USO'),
    ('T215', 'Continental', 99.00, 'EM_USO'),
    ('T216', 'Michelin', 100.20, 'EM_USO'),
    ('T217', 'Bridgestone', 98.20, 'EM_USO'),
    ('T218', 'Goodyear', 96.50, 'EM_USO'),
    ('T219', 'Pirelli', 102.50, 'EM_USO'),
    ('T220', 'Continental', 100.80, 'EM_USO'),
    ('T221', 'Michelin', 99.80, 'EM_USO'),
    ('T222', 'Bridgestone', 97.80, 'EM_USO'),
    ('T223', 'Goodyear', 98.80, 'EM_USO'),
    ('T224', 'Pirelli', 101.20, 'EM_USO'),
    ('T225', 'Continental', 99.20, 'EM_USO'),
    ('T226', 'Michelin', 100.40, 'EM_USO'),
    ('T227', 'Bridgestone', 98.40, 'EM_USO'),
    ('T228', 'Goodyear', 97.20, 'EM_USO'),
    ('T229', 'Pirelli', 102.20, 'EM_USO'),
    ('T230', 'Continental', 100.20, 'EM_USO'),
    ('T231', 'Michelin', 99.20, 'EM_USO'),
    ('T232', 'Bridgestone', 98.60, 'EM_USO'),
    ('T233', 'Goodyear', 97.60, 'EM_USO'),
    ('T234', 'Pirelli', 101.60, 'EM_USO'),
    ('T235', 'Continental', 99.60, 'EM_USO'),
    ('T236', 'Michelin', 100.60, 'EM_USO'),
    ('T237', 'Bridgestone', 98.90, 'EM_USO');

-- ABC1D23 (id 1): 6 em aberto — pneus 1,2,4,5,6,7 posições A,B,D,E,F,G (C livre para teste)
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'A', CURRENT_TIMESTAMP - INTERVAL '30 days' FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '188';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'B', CURRENT_TIMESTAMP - INTERVAL '29 days' FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '189';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'D', CURRENT_TIMESTAMP - INTERVAL '28 days' FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '191';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'E', CURRENT_TIMESTAMP - INTERVAL '27 days' FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = '192';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'F', CURRENT_TIMESTAMP - INTERVAL '26 days' FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = 'T201';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'G', CURRENT_TIMESTAMP - INTERVAL '25 days' FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = 'T202';

-- XYZ9K87 (id 2): pneus 8–13
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'P1_A', CURRENT_TIMESTAMP - INTERVAL '20 days' FROM veiculo v, pneu p WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = 'T203';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'P1_B', CURRENT_TIMESTAMP - INTERVAL '19 days' FROM veiculo v, pneu p WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = 'T204';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'P1_C', CURRENT_TIMESTAMP - INTERVAL '18 days' FROM veiculo v, pneu p WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = 'T205';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'P1_D', CURRENT_TIMESTAMP - INTERVAL '17 days' FROM veiculo v, pneu p WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = 'T206';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'P1_E', CURRENT_TIMESTAMP - INTERVAL '16 days' FROM veiculo v, pneu p WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = 'T207';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'P1_F', CURRENT_TIMESTAMP - INTERVAL '15 days' FROM veiculo v, pneu p WHERE v.placa = 'XYZ9K87' AND p.numero_fogo = 'T208';

-- TRK1880 (id 3): 6 em aberto
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'EIXO1_A', CURRENT_TIMESTAMP - INTERVAL '14 days' FROM veiculo v, pneu p WHERE v.placa = 'TRK1880' AND p.numero_fogo = 'T209';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'EIXO1_B', CURRENT_TIMESTAMP - INTERVAL '13 days' FROM veiculo v, pneu p WHERE v.placa = 'TRK1880' AND p.numero_fogo = 'T210';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'EIXO2_A', CURRENT_TIMESTAMP - INTERVAL '12 days' FROM veiculo v, pneu p WHERE v.placa = 'TRK1880' AND p.numero_fogo = 'T211';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'EIXO2_B', CURRENT_TIMESTAMP - INTERVAL '11 days' FROM veiculo v, pneu p WHERE v.placa = 'TRK1880' AND p.numero_fogo = 'T212';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'EIXO3_A', CURRENT_TIMESTAMP - INTERVAL '10 days' FROM veiculo v, pneu p WHERE v.placa = 'TRK1880' AND p.numero_fogo = 'T213';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'EIXO3_B', CURRENT_TIMESTAMP - INTERVAL '9 days' FROM veiculo v, pneu p WHERE v.placa = 'TRK1880' AND p.numero_fogo = 'T214';

-- RIO2A44 (id 4): 6 em aberto
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'R1_A', CURRENT_TIMESTAMP - INTERVAL '8 days' FROM veiculo v, pneu p WHERE v.placa = 'RIO2A44' AND p.numero_fogo = 'T215';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'R1_B', CURRENT_TIMESTAMP - INTERVAL '7 days' FROM veiculo v, pneu p WHERE v.placa = 'RIO2A44' AND p.numero_fogo = 'T216';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'R2_A', CURRENT_TIMESTAMP - INTERVAL '6 days' FROM veiculo v, pneu p WHERE v.placa = 'RIO2A44' AND p.numero_fogo = 'T217';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'R2_B', CURRENT_TIMESTAMP - INTERVAL '5 days' FROM veiculo v, pneu p WHERE v.placa = 'RIO2A44' AND p.numero_fogo = 'T218';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'R3_A', CURRENT_TIMESTAMP - INTERVAL '4 days' FROM veiculo v, pneu p WHERE v.placa = 'RIO2A44' AND p.numero_fogo = 'T219';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'R3_B', CURRENT_TIMESTAMP - INTERVAL '3 days' FROM veiculo v, pneu p WHERE v.placa = 'RIO2A44' AND p.numero_fogo = 'T220';

-- SPB9F11 (id 5): 6 em aberto
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'S_A1', CURRENT_TIMESTAMP - INTERVAL '2 days' FROM veiculo v, pneu p WHERE v.placa = 'SPB9F11' AND p.numero_fogo = 'T221';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'S_A2', CURRENT_TIMESTAMP - INTERVAL '2 days' FROM veiculo v, pneu p WHERE v.placa = 'SPB9F11' AND p.numero_fogo = 'T222';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'S_B1', CURRENT_TIMESTAMP - INTERVAL '2 days' FROM veiculo v, pneu p WHERE v.placa = 'SPB9F11' AND p.numero_fogo = 'T223';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'S_B2', CURRENT_TIMESTAMP - INTERVAL '2 days' FROM veiculo v, pneu p WHERE v.placa = 'SPB9F11' AND p.numero_fogo = 'T224';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'S_C1', CURRENT_TIMESTAMP - INTERVAL '2 days' FROM veiculo v, pneu p WHERE v.placa = 'SPB9F11' AND p.numero_fogo = 'T225';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'S_C2', CURRENT_TIMESTAMP - INTERVAL '2 days' FROM veiculo v, pneu p WHERE v.placa = 'SPB9F11' AND p.numero_fogo = 'T226';

-- CUR3B77 (id 6): 6 em aberto
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'C_A1', CURRENT_TIMESTAMP - INTERVAL '1 days' FROM veiculo v, pneu p WHERE v.placa = 'CUR3B77' AND p.numero_fogo = 'T227';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'C_A2', CURRENT_TIMESTAMP - INTERVAL '1 days' FROM veiculo v, pneu p WHERE v.placa = 'CUR3B77' AND p.numero_fogo = 'T228';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'C_B1', CURRENT_TIMESTAMP - INTERVAL '1 days' FROM veiculo v, pneu p WHERE v.placa = 'CUR3B77' AND p.numero_fogo = 'T229';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'C_B2', CURRENT_TIMESTAMP - INTERVAL '1 days' FROM veiculo v, pneu p WHERE v.placa = 'CUR3B77' AND p.numero_fogo = 'T230';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'C_C1', CURRENT_TIMESTAMP - INTERVAL '1 days' FROM veiculo v, pneu p WHERE v.placa = 'CUR3B77' AND p.numero_fogo = 'T231';
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo)
SELECT v.id, p.id, 'C_C2', CURRENT_TIMESTAMP - INTERVAL '1 days' FROM veiculo v, pneu p WHERE v.placa = 'CUR3B77' AND p.numero_fogo = 'T232';

-- Registro encerrado — ABC1D23 já teve outro pneu na posição C
INSERT INTO veiculo_pneu (veiculo_id, pneu_id, posicao, data_vinculo, data_desvinculo)
SELECT v.id, p.id, 'C', CURRENT_TIMESTAMP - INTERVAL '400 days', CURRENT_TIMESTAMP - INTERVAL '200 days'
FROM veiculo v, pneu p WHERE v.placa = 'ABC1D23' AND p.numero_fogo = 'T233';
