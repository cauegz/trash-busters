-- ==========================================
-- SEED INICIAL - TRASHBUSTERS (CANOAS/RS)
-- ==========================================

-- 1. Inserir Papéis (Roles) dos Usuários
INSERT INTO papel (nome) VALUES
     ('ROLE_ADMIN'),
     ('ROLE_ADMIN');

-- 2. Inserir Status possíveis para os Inimigos/Bosses
INSERT INTO status (nome) VALUES
      ('ATIVO'),
      ('DERROTADO');

-- 3. Inserir Bairros principais de Canoas
INSERT INTO bairro (nome) VALUES
      ('Mathias Velho'),
      ('Guajuviras'),
      ('Niterói'),
      ('Centro'),
      ('Harmonia');

-- 4. Inserir os Bosses (Inimigos) vinculados aos seus respectivos bairros e status inicial (ID 1 = ATIVO)
INSERT INTO inimigo (nome, hp_max, hp_atual, bairro_id, status_id) VALUES
    ('EntulhoSauro', 1500, 1500, 1, 1),
    ('Monstro do Plástico', 2000, 2000, 2, 1),
    ('Kraken dos Entulhos', 1200, 1200, 3, 1),
    ('Lord Pneu', 1000, 1000, 4, 1),
    ('Fumaça Negra', 1300, 1300, 5, 1);