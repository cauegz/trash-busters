# Trash Busters — Front-End (React + Vite + Tailwind + Leaflet)

Front-end gamificado para a plataforma **Trash Busters (Canoas/RS)**, integrado com a API Spring Boot (`http://localhost:8080`).

## Funcionalidades

- **Autenticação JWT (`/api/v1/auth/login`, `/api/v1/auth/register`, `/api/v1/auth/me`)**: Login, criação de conta, HUD de XP e patente do jogador.
- **Radar Geoespacial PostGIS (`CanoasMap`)**: Mapa interativo Leaflet com os polígonos oficiais dos 18 bairros de Canoas (`bairros.geojson`), coloridos dinamicamente pelo status e vida do Boss de cada bairro.
- **Arena de Bosses (`/api/v1/bairros`, `/api/v1/combat/active`, `/api/v1/combat/defeated`)**: Visualização em tempo real do HP de cada Boss (EntulhoSauro, Monstro do Plástico, Kraken dos Entulhos, Lord Pneu, Fumaça Negra, etc.) com filtros e busca.
- **Console de Ataque / Nova Denúncia (`POST /api/v1/reports`)**:
  - Envio de `multipart/form-data` com `imagem`, `latitude`, `longitude` e `endereco`.
  - Seleção de coordenadas clicando diretamente no mapa de Canoas, usando o GPS do navegador ou escolhendo um dos 18 bairros.
  - Gerador integrado de foto de teste (Canvas) para testes rápidos sem precisar de imagem externa.
  - Feedback de combate exibindo validação da IA Multimodal, categoria do resíduo, severidade, justificativa da IA e dano causado ao Boss.
- **Histórico de Combate (`GET /api/v1/reports/mine`)**: Lista todas as denúncias enviadas pelo jogador.
- **Modo Simulação Interativo**: Permite testar todo o fluxo de combate, dano e XP mesmo quando o backend ou o banco PostGIS não estão rodando.

## Como Executar

`VITE_API_URL` pode ser usada para apontar o frontend para uma API publicada
(por exemplo, `https://trashbusters-api.onrender.com`). Em desenvolvimento, o
padrão é `http://localhost:8080`; em builds de produção sem essa variável, o
frontend usa a mesma origem da página, permitindo publicar a API atrás do
mesmo domínio sem apontar para o `localhost` do usuário.

### Opção 1: Com Node.js (Vite Dev Server)
```bash
cd frontend
npm install
npm run dev
```
Acesse: `http://localhost:5173`

### Opção 2: Com Docker Compose (na raiz do projeto)
```bash
docker compose up --build
```
- Front-End: `http://localhost:5173`
- API Spring Boot: `http://localhost:8080`

### Chave das tiles do mapa

As tiles do CARTO exigem uma chave de API. Solicite uma chave em
`https://carto.com/basemaps/apikey/` e defina `VITE_CARTO_API_KEY` no ambiente
de build. Para desenvolvimento local, crie `frontend/.env.local` (esse arquivo
não deve ser versionado):

```env
VITE_CARTO_API_KEY=sua-chave-do-carto
```

Na Vercel, configure essa variável para o ambiente de produção. Restrinja a
chave ao domínio `trash-busters-six.vercel.app` e, se desejar usar o mapa
localmente, também a `localhost` e `127.0.0.1`.
