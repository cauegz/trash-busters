import { BAIRROS_PRESETS, CATEGORIAS_LIXO } from '../data/canoasBairros';

const DEFAULT_API_BASE =
  import.meta.env.VITE_API_URL ||
  (import.meta.env.DEV ? 'http://localhost:8080' : '');

const STORAGE_KEYS = {
  TOKEN: 'trashbusters_jwt',
  DEMO_MODE: 'trashbusters_demo_mode',
  DEMO_STATE: 'trashbusters_demo_state',
};

function getInitialDemoState() {
  const saved = localStorage.getItem(STORAGE_KEYS.DEMO_STATE);
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch {
      // ignore invalid json
    }
  }
  const bairros = BAIRROS_PRESETS.map((b, idx) => {
    const preDamaged = idx === 0 ? 420 : idx === 1 ? 650 : idx === 3 ? 1000 : 0;
    const hpAtual = Math.max(0, b.hpMax - preDamaged);
    return {
      id: b.id,
      codigoIbge: `43046060${String(b.id).padStart(2, '0')}`,
      nome: b.nome,
      inimigo: {
        id: b.id,
        nome: b.bossDefault,
        statusInimigo: hpAtual === 0 ? 'DERROTADO' : 'ATIVO',
        hpAtual,
        hpMaximo: b.hpMax,
        bairro: b.nome,
      },
    };
  });

  return {
    user: {
      nome: 'Agente Canoas (Demo)',
      login: 'teste',
      xp: 240,
      papel: ['ROLE_PLAYER'],
    },
    bairros,
    reports: [
      {
        justificativaIa: 'Descarte irregular de entulho de construção e sacos plásticos na calçada.',
        severidade: 4,
        valido: true,
        categoria: 'ENTULHO',
        dano: 130,
        latitude: -29.895,
        longitude: -51.193,
        inimigo: bairros[0].inimigo,
      },
      {
        justificativaIa: 'Acúmulo de garrafas PET e embalagens plásticas próximo ao bueiro.',
        severidade: 5,
        valido: true,
        categoria: 'PLASTICO',
        dano: 110,
        latitude: -29.9035,
        longitude: -51.1405,
        inimigo: bairros[1].inimigo,
      },
    ],
  };
}

function saveDemoState(state) {
  localStorage.setItem(STORAGE_KEYS.DEMO_STATE, JSON.stringify(state));
}

export function getToken() {
  return localStorage.getItem(STORAGE_KEYS.TOKEN);
}

export function setToken(token) {
  if (token) {
    localStorage.setItem(STORAGE_KEYS.TOKEN, token);
  } else {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
  }
}

export function isDemoMode() {
  return localStorage.getItem(STORAGE_KEYS.DEMO_MODE) === 'true';
}

export function setDemoMode(enabled) {
  if (enabled) {
    localStorage.setItem(STORAGE_KEYS.DEMO_MODE, 'true');
    if (!getToken()) {
      setToken('demo-jwt-token');
    }
  } else {
    localStorage.removeItem(STORAGE_KEYS.DEMO_MODE);
  }
}

export function getApiBaseUrl() {
  return localStorage.getItem('trashbusters_api_url') || DEFAULT_API_BASE;
}

export function setApiBaseUrl(url) {
  if (url) {
    localStorage.setItem('trashbusters_api_url', url.replace(/\/+$/, ''));
  } else {
    localStorage.removeItem('trashbusters_api_url');
  }
}

async function request(path, options = {}) {
  const baseUrl = getApiBaseUrl();
  const token = getToken();
  const headers = { ...(options.headers || {}) };

  if (token && !headers.Authorization) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (!(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers,
  });

  const text = await response.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = { mensagem: text };
    }
  }

  if (!response.ok) {
    const err = new Error(
      data?.mensagem || data?.justificativaIa || data?.erro || `Erro HTTP ${response.status}`
    );
    err.status = response.status;
    err.data = data;
    throw err;
  }

  return data;
}

export const api = {
  async login(login, password) {
    if (isDemoMode()) {
      const state = getInitialDemoState();
      state.user.login = login || 'teste';
      saveDemoState(state);
      const token = 'demo-jwt-token';
      setToken(token);
      return { token };
    }
    const res = await request('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ login, password }),
    });
    if (res?.token) {
      setToken(res.token);
    }
    return res;
  },

  async register(nome, login, password) {
    if (isDemoMode()) {
      const state = getInitialDemoState();
      state.user = { nome, login, xp: 0, papel: ['ROLE_PLAYER'] };
      saveDemoState(state);
      return { ok: 'true', mensagem: 'usuário criado com sucesso (Modo Demo)' };
    }
    return request('/api/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify({ nome, login, password }),
    });
  },

  async getMe() {
    if (isDemoMode()) {
      return getInitialDemoState().user;
    }
    return request('/api/v1/auth/me');
  },

  async getBairros() {
    if (isDemoMode()) {
      return getInitialDemoState().bairros;
    }
    return request('/api/v1/bairros');
  },

  async getBairroById(id) {
    if (isDemoMode()) {
      const found = getInitialDemoState().bairros.find((b) => Number(b.id) === Number(id));
      if (!found) throw new Error('Bairro não encontrado');
      return found;
    }
    return request(`/api/v1/bairros/${id}`);
  },

  async getActiveEnemies() {
    if (isDemoMode()) {
      return getInitialDemoState()
        .bairros.map((b) => b.inimigo)
        .filter((i) => i.statusInimigo === 'ATIVO');
    }
    return request('/api/v1/combat/active');
  },

  async getDefeatedEnemies() {
    if (isDemoMode()) {
      return getInitialDemoState()
        .bairros.map((b) => b.inimigo)
        .filter((i) => i.statusInimigo === 'DERROTADO');
    }
    return request('/api/v1/combat/defeated');
  },

  async getMyReports() {
    if (isDemoMode()) {
      return getInitialDemoState().reports;
    }
    return request('/api/v1/reports/mine');
  },

  async sendReport({ endereco, latitude, longitude, imagem, demoCategoria = 'ENTULHO', demoSeveridade = 4 }) {
    if (isDemoMode()) {
      await new Promise((r) => setTimeout(r, 900));
      const state = getInitialDemoState();
      // Encontra o bairro mais próximo pela coordenada
      let closestBairro = state.bairros[0];
      let minDist = Infinity;
      BAIRROS_PRESETS.forEach((preset) => {
        const dist = Math.hypot(preset.lat - latitude, preset.lng - longitude);
        if (dist < minDist) {
          minDist = dist;
          const b = state.bairros.find((item) => item.nome === preset.nome);
          if (b) closestBairro = b;
        }
      });

      const mult = CATEGORIAS_LIXO[demoCategoria]?.mult || 1.2;
      const dano = Math.floor(50 * mult + demoSeveridade * 10);

      if (closestBairro.inimigo.statusInimigo !== 'DERROTADO') {
        closestBairro.inimigo.hpAtual = Math.max(0, closestBairro.inimigo.hpAtual - dano);
        if (closestBairro.inimigo.hpAtual === 0) {
          closestBairro.inimigo.statusInimigo = 'DERROTADO';
        }
        state.user.xp += dano;
      }

      const reportResponse = {
        justificativaIa: `A análise identificou descarte irregular de ${CATEGORIAS_LIXO[demoCategoria]?.label || demoCategoria} com nível de impacto ${demoSeveridade}/5 em ${closestBairro.nome}.`,
        severidade: demoSeveridade,
        valido: true,
        categoria: demoCategoria,
        dano,
        latitude: Number(latitude),
        longitude: Number(longitude),
        inimigo: { ...closestBairro.inimigo },
      };

      state.reports = [reportResponse, ...state.reports];
      saveDemoState(state);
      return reportResponse;
    }

    const formData = new FormData();
    formData.append('endereco', endereco);
    formData.append('latitude', String(latitude));
    formData.append('longitude', String(longitude));
    formData.append('imagem', imagem);

    const baseUrl = getApiBaseUrl();
    const token = getToken();
    const response = await fetch(`${baseUrl}/api/v1/reports`, {
      method: 'POST',
      headers: token ? { Authorization: `Bearer ${token}` } : {},
      body: formData,
    });

    const data = await response.json().catch(() => null);

    // Quando a IA rejeita a imagem, a API retorna 400 com ReportResponseDTO (valido = false)
    if (response.status === 400 && data && typeof data.valido === 'boolean') {
      return data;
    }

    if (!response.ok) {
      const err = new Error(
        data?.mensagem || data?.erro || `Falha ao enviar denúncia (HTTP ${response.status})`
      );
      err.status = response.status;
      err.data = data;
      throw err;
    }

    return data;
  },

  resetDemoState() {
    localStorage.removeItem(STORAGE_KEYS.DEMO_STATE);
    return getInitialDemoState();
  },
};
