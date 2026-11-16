-- ==========================================
-- SEED INICIAL - TRASHBUSTERS (CANOAS/RS)
-- ==========================================

-- 1. Inserir Papéis (Roles) dos Usuários
INSERT INTO papel (nome) VALUES
     ('ROLE_ADMIN'),
     ('ROLE_PLAYER')
ON CONFLICT (nome) DO NOTHING;

-- 2. Usuário administrador inicial
INSERT INTO usuario (login, senha, nome, xp, papel_id)
OVERRIDING SYSTEM VALUE
VALUES (
    'teste',
    '$2b$10$1To17ygqbNjodkfAC01Yx.o58PljAEvXDCrRd79fRARd1rV/gM1eC',
    'Teste da Silva',
    0,
    1
)
ON CONFLICT (id) DO NOTHING;

-- 3. Inserir Status possíveis para os Inimigos/Bosses
INSERT INTO status (nome) VALUES
      ('ATIVO'),
      ('DERROTADO')
ON CONFLICT (nome) DO NOTHING;