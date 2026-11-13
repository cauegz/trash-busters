# Trash Busters

## Executar com Docker

O único arquivo de configuração necessário é o `.env`. Crie-o a partir do
modelo e informe a chave gerada no Google AI Studio:

```bash
cp .env.example .env
```

Preencha pelo menos `GOOGLE_API_KEY` e, de preferência,
`API_SECURITY_TOKEN_SECRET` com um valor aleatório longo. Não versione o
arquivo `.env`.

Inicie API e PostgreSQL:

```bash
docker compose up --build
```

A API ficará disponível em `http://localhost:8080` e o PostgreSQL em
`localhost:5432`. Os dados do banco são persistidos no volume `pgdata`.

Para encerrar os containers, mantendo os dados:

```bash
docker compose down
```

Para reiniciar o banco do zero, incluindo o volume persistido:

```bash
docker compose down -v
```
