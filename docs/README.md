# Trash Busters

Backend da plataforma Trash Busters, desenvolvido em **Java 21**, **Spring Boot** e
**PostgreSQL/PostGIS**. O sistema recebe denúncias de descarte irregular, usa o
um provedor multimodal por meio do Spring AI para validar as imagens e aplica dano ao boss do
bairro correspondente.

## Funcionalidades

- Cadastro, login e autenticação stateless com JWT.
- Envio de denúncia com imagem, endereço, latitude e longitude.
- Validação multimodal da imagem pelo modelo configurado.
- Classificação da categoria e severidade do lixo.
- Persistência das denúncias com status `VALIDATED` ou `REJECTED_AI`.
- Associação da denúncia ao bairro e ao boss correspondente.
- Estrutura de combate com dano, vida do boss e logs de combate.
- Consultas espaciais com PostGIS.

## Tecnologias

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.1 |
| IA | Spring AI + adaptador de provedor configurável |
| Persistência | Spring Data JPA + Hibernate Spatial |
| Banco de dados | PostgreSQL 15 + PostGIS 3.3 |
| Segurança | Spring Security + JWT |
| Build | Maven |

## Pré-requisitos

- Docker e Docker Compose; ou
- Java 21, Maven e uma instância PostgreSQL com PostGIS.
- Uma chave de API do provedor de IA configurado.

## Configuração com Docker

Crie o arquivo `.env` a partir do modelo:

```bash
cp .env.example .env
```

Defina pelo menos `AI_API_KEY` e `API_SECURITY_TOKEN_SECRET`. O arquivo
`.env` não deve ser versionado.

Suba a API e o banco:

```bash
docker compose up --build
```

Serviços disponíveis:

- API: `http://localhost:8080`
- PostgreSQL/PostGIS: `localhost:5432`

Os scripts em `../database/init` são executados automaticamente na primeira
inicialização do volume do PostgreSQL. Para recriar o banco do zero:

```bash
docker compose down -v
docker compose up --build
```

## Variáveis de ambiente

| Variável | Obrigatória | Descrição |
|---|---:|---|
| `AI_API_KEY` | Sim | Chave do provedor de IA configurado |
| `API_SECURITY_TOKEN_SECRET` | Recomendada | Segredo usado para assinar JWTs |
| `AI_MODEL` | Não | Nome do modelo usado pelo provedor configurado |
| `APP_PORT` | Não | Porta publicada pela API; padrão `8080` |
| `POSTGRES_PORT` | Não | Porta publicada pelo banco; padrão `5432` |
| `POSTGRES_USER` | Não | Usuário do PostgreSQL |
| `POSTGRES_PASSWORD` | Não | Senha do PostgreSQL |
| `POSTGRES_DB` | Não | Nome do banco |

## Principais endpoints

### Autenticação

| Método | Endpoint | Autenticação |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Pública |
| `POST` | `/api/v1/auth/login` | Pública |
| `GET` | `/api/v1/auth/me` | Bearer JWT |

### Denúncias

| Método | Endpoint | Autenticação |
|---|---|---|
| `POST` | `/api/v1/reports` | Bearer JWT |

O endpoint de denúncia recebe `multipart/form-data` com os campos `imagem`,
`latitude`, `longitude` e `endereco`. A imagem é enviada ao provedor configurado; denúncias
válidas são armazenadas e associadas ao boss do bairro identificado pela
coordenada.

Exemplo de requisição:

```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer SEU_TOKEN" \
  -F "imagem=@./exemplo.jpg" \
  -F "latitude=-29.9178" \
  -F "longitude=-51.1837" \
  -F "endereco=Rua Exemplo, 100"
```

## Estrutura do projeto

```text
src/main/java/.../
├── controllers/       # Endpoints HTTP
├── domain/            # Entidades, DTOs, enums e repositórios
├── services/          # Regras de negócio, IA e armazenamento
└── infra/             # Segurança, configuração e tratamento de erros
database/init/         # Schema, seeds e dados iniciais do PostgreSQL
docs/                  # Requisitos e modelo ERD
trash/                 # Coleção de requisições para testes manuais
```

## Provedores de IA e fallback

O domínio não depende de um fornecedor específico. O fluxo usa:

1. `AiProvider`, contrato que cada integração de IA implementa.
2. `SpringAiProvider`, adaptador do provedor atualmente instalado.
3. `AiProviderChain`, que tenta os provedores na ordem definida por `@Order`.

Se o provedor principal lançar uma exceção, o próximo `AiProvider` é acionado
automaticamente. Para adicionar um segundo fornecedor, instale o starter do
Spring AI correspondente, crie outro bean que implemente `AiProvider` e defina
uma ordem maior que a do provedor principal. O controller e as regras de
negócio não precisam ser alterados.

## Banco de dados

O schema oficial está em `../database/init/01-schema.sql`. Ele habilita o PostGIS,
cria as tabelas de usuários, papéis, bairros, denúncias, bosses, status e logs
de combate, além dos índices espaciais. O diagrama correspondente está em
`modeloBanco.erd` e pode ser aberto no [ERD Editor](https://erd-editor.io/).

## Desenvolvimento e testes

Para executar os testes:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

Para executar a aplicação localmente, configure as variáveis do banco e da IA e
use:

```powershell
.\mvnw.cmd spring-boot:run
```

## Documentação relacionada

- [Requisitos do sistema](Requisitos.md)
- [Schema SQL](../database/init/01-schema.sql)
- [Modelo do banco](modeloBanco.erd)
- [Coleção de requisições](trash/)
