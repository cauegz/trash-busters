CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS bairro
(
    id   INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS denuncia_lixo
(
    id          INT                   NOT NULL GENERATED ALWAYS AS IDENTITY,
    imagem      VARCHAR(255)         ,
    criado_em   TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    localizacao GEOMETRY(Point, 4326) NOT NULL,
    usuario_id  INT                   NOT NULL,
    inimigo_id  INT                   NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS inimigo
(
    id        INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    nome      VARCHAR(100) NOT NULL UNIQUE,
    hp_max    INT          NOT NULL DEFAULT 100,
    hp_atual  INT          NOT NULL DEFAULT 100,
    bairro_id INT          NOT NULL,
    status_id INT          NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS log_combate
(
    id               INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    dano_realizado   INT,
    usuario_id       INT NOT NULL,
    denuncia_lixo_id INT NOT NULL,
    inimigo_id       INT NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS papel
(
    id   INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS status
(
    id   INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS usuario
(
    id       INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    login    VARCHAR(100) NOT NULL UNIQUE,
    senha    VARCHAR(255) NOT NULL,
    nome     VARCHAR(100),
    papel_id INT          NOT NULL,
    PRIMARY KEY (id)
    );

ALTER TABLE usuario DROP CONSTRAINT IF EXISTS FK_papel_TO_usuario;
ALTER TABLE usuario
    ADD CONSTRAINT FK_papel_TO_usuario
        FOREIGN KEY (papel_id)
            REFERENCES papel (id);

ALTER TABLE denuncia_lixo DROP CONSTRAINT IF EXISTS FK_usuario_TO_denuncia_lixo;
ALTER TABLE denuncia_lixo
    ADD CONSTRAINT FK_usuario_TO_denuncia_lixo
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id);

ALTER TABLE denuncia_lixo DROP CONSTRAINT IF EXISTS FK_inimigo_TO_denuncia_lixo;
ALTER TABLE denuncia_lixo
    ADD CONSTRAINT FK_inimigo_TO_denuncia_lixo
        FOREIGN KEY (inimigo_id)
            REFERENCES inimigo (id);

ALTER TABLE inimigo DROP CONSTRAINT IF EXISTS FK_bairro_TO_inimigo;
ALTER TABLE inimigo
    ADD CONSTRAINT FK_bairro_TO_inimigo
        FOREIGN KEY (bairro_id)
            REFERENCES bairro (id);

ALTER TABLE log_combate DROP CONSTRAINT IF EXISTS FK_usuario_TO_log_combate;
ALTER TABLE log_combate
    ADD CONSTRAINT FK_usuario_TO_log_combate
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id);

ALTER TABLE log_combate DROP CONSTRAINT IF EXISTS FK_denuncia_lixo_TO_log_combate;
ALTER TABLE log_combate
    ADD CONSTRAINT FK_denuncia_lixo_TO_log_combate
        FOREIGN KEY (denuncia_lixo_id)
            REFERENCES denuncia_lixo (id);

ALTER TABLE log_combate DROP CONSTRAINT IF EXISTS FK_inimigo_TO_log_combate;
ALTER TABLE log_combate
    ADD CONSTRAINT FK_inimigo_TO_log_combate
        FOREIGN KEY (inimigo_id)
            REFERENCES inimigo (id);

ALTER TABLE inimigo DROP CONSTRAINT IF EXISTS FK_status_TO_inimigo;
ALTER TABLE inimigo
    ADD CONSTRAINT FK_status_TO_inimigo
        FOREIGN KEY (status_id)
            REFERENCES status (id);
