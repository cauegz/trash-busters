-- ==========================================
-- SEED INICIAL - TRASHBUSTERS (CANOAS/RS)
-- ==========================================

-- 1. Inserir Papéis (Roles) dos Usuários
INSERT INTO papel (nome) VALUES
     ('ROLE_ADMIN'),
     ('ROLE_PLAYER')
ON CONFLICT (nome) DO NOTHING;

-- 2. Inserir Status possíveis para os Inimigos/Bosses
INSERT INTO status (nome) VALUES
      ('ATIVO'),
      ('DERROTADO')
ON CONFLICT (nome) DO NOTHING;