import React, { useEffect, useState, useCallback } from 'react';
import {
  Shield,
  Swords,
  Trophy,
  History,
  RefreshCw,
  LogOut,
  Sparkles,
  Search,
  MapPin,
  Zap,
  CheckCircle2,
  XCircle,
  Info,
  RotateCcw,
} from 'lucide-react';
import AuthScreen from './components/AuthScreen';
import CanoasMap from './components/CanoasMap';
import ReportForm from './components/ReportForm';
import {
  api,
  getToken,
  setToken,
  isDemoMode,
  setDemoMode,
} from './services/api';
import {
  CATEGORIAS_LIXO,
  getBairroMeta,
  getPlayerRank,
} from './data/canoasBairros';

export default function App() {
  const [authenticated, setAuthenticated] = useState(Boolean(getToken()));
  const [user, setUser] = useState(null);
  const [bairros, setBairros] = useState([]);
  const [reports, setReports] = useState([]);
  const [selectedBairro, setSelectedBairro] = useState(null);
  const [selectedCoords, setSelectedCoords] = useState({
    latitude: -29.8950,
    longitude: -51.1930,
    endereco: 'Av. Rio Grande do Sul, 1500 - Mathias Velho',
    bairroNome: 'Mathias Velho',
  });
  const [bossFilter, setBossFilter] = useState('ALL'); // 'ALL' | 'ATIVO' | 'DERROTADO'
  const [searchQuery, setSearchQuery] = useState('');
  const [loadingData, setLoadingData] = useState(false);
  const [apiWarning, setApiWarning] = useState('');

  const loadDashboardData = useCallback(async () => {
    if (!getToken()) return;
    setLoadingData(true);
    setApiWarning('');

    try {
      const [meData, bairrosData, myReports] = await Promise.all([
        api.getMe(),
        api.getBairros(),
        api.getMyReports().catch(() => []),
      ]);

      setUser(meData);
      setBairros(Array.isArray(bairrosData) ? bairrosData : []);
      setReports(Array.isArray(myReports) ? myReports : []);

      if (Array.isArray(bairrosData) && bairrosData.length > 0) {
        setSelectedBairro((prev) => {
          if (prev) {
            const updated = bairrosData.find((b) => b.id === prev.id);
            if (updated) return updated;
          }
          return bairrosData[0];
        });
      }
    } catch (err) {
      if (err.status === 401 || err.status === 403) {
        setToken(null);
        setAuthenticated(false);
      } else {
        setApiWarning(
          'Não foi possível atualizar as informações agora. Você pode continuar usando o modo de demonstração.'
        );
      }
    } finally {
      setLoadingData(false);
    }
  }, []);

  useEffect(() => {
    if (authenticated) {
      loadDashboardData();
    }
  }, [authenticated, loadDashboardData]);

  const handleLogout = () => {
    setToken(null);
    setDemoMode(false);
    setAuthenticated(false);
    setUser(null);
  };

  const handleSwitchToDemo = async () => {
    setDemoMode(true);
    await api.login('teste', 'demo');
    loadDashboardData();
  };

  const handleResetDemo = () => {
    api.resetDemoState();
    loadDashboardData();
  };

  const handleSelectBairro = (bairroObj) => {
    setSelectedBairro(bairroObj);
    const meta = getBairroMeta(bairroObj?.nome);
    if (meta) {
      setSelectedCoords({
        latitude: meta.lat,
        longitude: meta.lng,
        endereco: meta.enderecoExemplo,
        bairroNome: bairroObj.nome,
      });
    }
  };

  if (!authenticated) {
    return <AuthScreen onAuthenticated={() => setAuthenticated(true)} />;
  }

  // Estatísticas globais de combate em Canoas
  const totalBosses = bairros.length;
  const defeatedBosses = bairros.filter(
    (b) => b.inimigo?.statusInimigo === 'DERROTADO' || b.inimigo?.hpAtual === 0
  ).length;
  const activeBosses = totalBosses - defeatedBosses;

  const totalHpMax = bairros.reduce(
    (acc, b) => acc + (b.inimigo?.hpMaximo || 1000),
    0
  );
  const totalHpAtual = bairros.reduce(
    (acc, b) => acc + (b.inimigo?.hpAtual ?? 1000),
    0
  );
  const cityCleanPercent =
    totalHpMax > 0
      ? Math.round(((totalHpMax - totalHpAtual) / totalHpMax) * 100)
      : 0;

  const playerRank = getPlayerRank(user?.xp || 0);

  const filteredBairros = bairros.filter((b) => {
    const status =
      b.inimigo?.statusInimigo === 'DERROTADO' || b.inimigo?.hpAtual === 0
        ? 'DERROTADO'
        : 'ATIVO';
    if (bossFilter !== 'ALL' && status !== bossFilter) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        b.nome?.toLowerCase().includes(q) ||
        b.inimigo?.nome?.toLowerCase().includes(q)
      );
    }
    return true;
  });

  return (
    <div className="app-shell min-h-screen bg-slate-50 text-slate-900 flex flex-col">
      {/* Top Navbar / HUD */}
      <header className="sticky top-0 z-30 bg-white/95 backdrop-blur border-b border-slate-200/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3 flex flex-wrap items-center justify-between gap-4">
          {/* Brand */}
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-emerald-600 text-white shadow-sm">
              <Shield className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-display font-bold text-lg tracking-tight text-slate-900">
                  TRASH BUSTERS
                </span>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-emerald-500/15 text-emerald-300 border border-emerald-500/30">
                  Canoas/RS
                </span>
                {isDemoMode() && (
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-amber-500/20 text-amber-300 border border-amber-500/40">
                    Demonstração
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-400">
                Sistema Geoespacial de Combate ao Descarte Irregular
              </p>
            </div>
          </div>

          {/* Player HUD & Actions */}
          <div className="flex flex-wrap items-center gap-3">
            {user && (
              <div className="flex items-center gap-3 px-3.5 py-1.5 rounded-xl bg-slate-900 border border-slate-800">
                <div className="text-right">
                  <div className="text-xs font-bold text-white flex items-center gap-1.5 justify-end">
                    <span>{user.nome || user.login}</span>
                    <span className="px-1.5 py-0.2 rounded bg-slate-800 text-[10px] font-mono text-emerald-400">
                      Nv.{playerRank.level}
                    </span>
                  </div>
                  <div className={`text-[11px] font-medium ${playerRank.color}`}>
                    {playerRank.title}
                  </div>
                </div>
                <div className="h-7 w-px bg-slate-800" />
                <div className="flex items-center gap-1.5">
                  <Zap className="w-4 h-4 text-amber-400" />
                  <span className="font-mono font-bold text-sm text-amber-300">
                    {user.xp ?? 0} XP
                  </span>
                </div>
              </div>
            )}

            {isDemoMode() && (
              <button
                type="button"
                onClick={handleResetDemo}
                title="Reiniciar dados da demonstração"
                className="p-2 rounded-lg bg-white hover:bg-slate-50 border border-slate-200 text-slate-600 text-xs inline-flex items-center gap-1.5 transition"
              >
                <RotateCcw className="w-4 h-4 text-amber-400" />
                <span className="hidden sm:inline">Reiniciar</span>
              </button>
            )}

            <button
              type="button"
              onClick={loadDashboardData}
              disabled={loadingData}
              title="Atualizar dados"
              className="p-2 rounded-lg bg-white hover:bg-slate-50 border border-slate-200 text-slate-600 transition"
            >
              <RefreshCw
                className={`w-4 h-4 ${loadingData ? 'animate-spin text-emerald-400' : ''}`}
              />
            </button>

            <button
              type="button"
              onClick={handleLogout}
              className="px-3 py-2 rounded-lg bg-white hover:bg-rose-50 border border-rose-200 text-rose-600 text-xs font-semibold inline-flex items-center gap-1.5 transition"
            >
              <LogOut className="w-4 h-4" />
              Sair
            </button>
          </div>
        </div>
      </header>

      {/* Alerta de indisponibilidade */}
      {apiWarning && (
        <div className="max-w-7xl mx-auto w-full px-4 sm:px-6 mt-4">
          <div className="p-4 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex flex-wrap items-center justify-between gap-3 text-xs text-amber-200">
            <span>{apiWarning}</span>
            <button
              type="button"
              onClick={handleSwitchToDemo}
              className="px-3 py-1.5 rounded-lg bg-amber-400 text-slate-950 font-bold hover:bg-amber-300 transition"
            >
              Continuar em modo de demonstração
            </button>
          </div>
        </div>
      )}

      {/* Resumo de Campanha de Canoas */}
      <section className="max-w-7xl mx-auto w-full px-4 sm:px-6 pt-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
          <div className="p-4 rounded-2xl bg-slate-900/90 border border-slate-800 flex items-center justify-between">
            <div>
              <span className="text-xs text-slate-400">Bosses Ativos em Canoas</span>
              <div className="font-display text-2xl font-bold text-rose-400 mt-0.5">
                {activeBosses} <span className="text-xs font-normal text-slate-500">/ {totalBosses}</span>
              </div>
            </div>
            <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400">
              <Swords className="w-6 h-6" />
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-slate-900/90 border border-slate-800 flex items-center justify-between">
            <div>
              <span className="text-xs text-slate-400">Bairros Libertados</span>
              <div className="font-display text-2xl font-bold text-emerald-400 mt-0.5">
                {defeatedBosses} <span className="text-xs font-normal text-slate-500">bairros limpos</span>
              </div>
            </div>
            <div className="p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">
              <Trophy className="w-6 h-6" />
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-slate-900/90 border border-slate-800 flex items-center justify-between">
            <div>
              <span className="text-xs text-slate-400">Minhas Denúncias</span>
              <div className="font-display text-2xl font-bold text-sky-400 mt-0.5">
                {reports.length}{' '}
                <span className="text-xs font-normal text-slate-500">
                  ({reports.filter((r) => r.valido).length} validadas)
                </span>
              </div>
            </div>
            <div className="p-3 rounded-xl bg-sky-500/10 border border-sky-500/20 text-sky-400">
              <Sparkles className="w-6 h-6" />
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-slate-900/90 border border-slate-800 flex flex-col justify-between">
            <div className="flex items-center justify-between text-xs">
              <span className="text-slate-400">Progresso Global da Cidade</span>
              <span className="font-mono font-bold text-emerald-400">
                {cityCleanPercent}%
              </span>
            </div>
            <div className="w-full h-2.5 rounded-full bg-slate-950 overflow-hidden border border-slate-800 my-2">
              <div
                className="h-full bg-gradient-to-r from-emerald-500 to-cyan-400 transition-all duration-500"
                style={{ width: `${cityCleanPercent}%` }}
              />
            </div>
            <div className="text-[11px] text-slate-500 font-mono">
              Impacto restante: {totalHpAtual.toLocaleString('pt-BR')} / {totalHpMax.toLocaleString('pt-BR')}
            </div>
          </div>
        </div>
      </section>

      {/* Grid Principal */}
      <main className="max-w-7xl mx-auto w-full px-4 sm:px-6 py-6 grid grid-cols-1 lg:grid-cols-12 gap-6 flex-1">
        {/* Coluna Esquerda (7 cols): Mapa + Boss em Destaque + Arena de Bairros */}
        <div className="lg:col-span-7 space-y-6">
          <CanoasMap
            bairros={bairros}
            selectedBairro={selectedBairro}
            onSelectBairro={handleSelectBairro}
            selectedCoords={selectedCoords}
            onSelectCoords={setSelectedCoords}
            reports={reports}
          />

          {/* Card do Boss do Bairro Selecionado */}
          {selectedBairro && (
            <div className="rounded-2xl border border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 p-5 shadow-xl">
              {(() => {
                const inimigo = selectedBairro.inimigo || {};
                const meta = getBairroMeta(selectedBairro.nome);
                const derrotado =
                  inimigo.statusInimigo === 'DERROTADO' || inimigo.hpAtual === 0;
                const pct = Math.max(
                  0,
                  Math.min(
                    100,
                    Math.round(
                      ((inimigo.hpAtual ?? 1000) / (inimigo.hpMaximo || 1000)) * 100
                    )
                  )
                );

                return (
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div className="flex items-start gap-4">
                      <div
                        className={`w-14 h-14 rounded-2xl flex items-center justify-center text-3xl border shrink-0 ${
                          derrotado
                            ? 'bg-emerald-500/10 border-emerald-500/40'
                            : 'bg-rose-500/10 border-rose-500/40'
                        }`}
                      >
                        {derrotado ? '🏆' : meta.emoji}
                      </div>
                      <div>
                        <div className="flex flex-wrap items-center gap-2">
                          <span className="text-xs font-mono uppercase px-2 py-0.5 rounded bg-slate-800 text-slate-300">
                            Bairro: {selectedBairro.nome}
                          </span>
                          <span
                            className={`text-[11px] font-bold px-2.5 py-0.5 rounded-full border ${
                              derrotado
                                ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40'
                                : 'bg-rose-500/20 text-rose-300 border-rose-500/40'
                            }`}
                          >
                            {derrotado ? 'DERROTADO (BAIRRO LIMPO)' : 'BOSS ATIVO'}
                          </span>
                        </div>
                        <h3 className="font-display text-xl font-bold text-white mt-1">
                          {inimigo.nome}
                        </h3>
                        <p className="text-xs text-slate-400">{meta.titulo}</p>
                      </div>
                    </div>

                    <div className="sm:w-64">
                      <div className="flex items-center justify-between text-xs font-mono mb-1.5">
                        <span className="text-slate-400">VIDA DO BOSS</span>
                        <span
                          className={
                            derrotado
                              ? 'text-emerald-400 font-bold'
                              : 'text-rose-400 font-bold'
                          }
                        >
                          {pct}% de progresso
                        </span>
                      </div>
                      <div className="w-full h-3.5 rounded-full bg-slate-950 border border-slate-800 overflow-hidden">
                        <div
                          className={`h-full transition-all duration-500 ${
                            derrotado
                              ? 'bg-emerald-500'
                              : pct < 50
                              ? 'bg-amber-500'
                              : 'bg-gradient-to-r from-rose-600 to-orange-500'
                          }`}
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                    </div>
                  </div>
                );
              })()}
            </div>
          )}

          {/* Lista de Todos os 18 Bairros & Bosses */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/80 p-5">
            <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
              <div>
                <h2 className="font-display text-lg font-bold text-white flex items-center gap-2">
                  <Swords className="w-5 h-5 text-rose-400" />
                  Arena de Bosses por Bairro ({filteredBairros.length})
                </h2>
                <p className="text-xs text-slate-400">
                  Colabore com os moradores do seu bairro para melhorar a cidade
                </p>
              </div>

              {/* Filtros Ativos / Derrotados */}
              <div className="flex items-center gap-1.5 bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs">
                {[
                  { id: 'ALL', label: `Todos (${totalBosses})` },
                  { id: 'ATIVO', label: `Ativos (${activeBosses})` },
                  { id: 'DERROTADO', label: `Derrotados (${defeatedBosses})` },
                ].map((tab) => (
                  <button
                    key={tab.id}
                    type="button"
                    onClick={() => setBossFilter(tab.id)}
                    className={`px-2.5 py-1 rounded-lg font-semibold transition ${
                      bossFilter === tab.id
                        ? 'bg-emerald-500 text-slate-950'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {tab.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Busca por bairro ou boss */}
            <div className="relative mb-4">
              <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-2.5" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Buscar por bairro (ex: Mathias Velho, Guajuviras, Centro) ou nome do Boss..."
                className="w-full pl-9 pr-4 py-2 rounded-xl bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:outline-none text-xs text-slate-200"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-[460px] overflow-y-auto pr-1">
              {filteredBairros.map((b) => {
                const inimigo = b.inimigo || {};
                const meta = getBairroMeta(b.nome);
                const derrotado =
                  inimigo.statusInimigo === 'DERROTADO' || inimigo.hpAtual === 0;
                const pct = Math.max(
                  0,
                  Math.min(
                    100,
                    Math.round(
                      ((inimigo.hpAtual ?? 1000) / (inimigo.hpMaximo || 1000)) * 100
                    )
                  )
                );
                const isSelected = selectedBairro?.id === b.id;

                return (
                  <button
                    key={b.id}
                    type="button"
                    onClick={() => handleSelectBairro(b)}
                    className={`text-left p-3.5 rounded-xl border transition flex flex-col justify-between gap-2.5 ${
                      isSelected
                        ? 'bg-slate-800/90 border-emerald-500/60 shadow-lg'
                        : 'bg-slate-950/70 hover:bg-slate-900 border-slate-800/90'
                    }`}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex items-center gap-2.5">
                        <span className="text-2xl">{derrotado ? '🏆' : meta.emoji}</span>
                        <div>
                          <div className="text-xs font-bold text-white leading-tight">
                            {inimigo.nome}
                          </div>
                          <div className="text-[11px] text-slate-400 flex items-center gap-1 mt-0.5">
                            <MapPin className="w-3 h-3 text-emerald-400" />
                            {b.nome}
                          </div>
                        </div>
                      </div>
                      <span
                        className={`px-2 py-0.5 rounded-full text-[10px] font-bold border ${
                          derrotado
                            ? 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30'
                            : 'bg-rose-500/15 text-rose-300 border-rose-500/30'
                        }`}
                      >
                        {derrotado ? 'LIMPO' : 'ATIVO'}
                      </span>
                    </div>

                    <div>
                      <div className="flex justify-between text-[11px] font-mono mb-1">
                        <span className="text-slate-400">Progresso</span>
                        <span className="text-slate-200">
                          {inimigo.hpAtual} / {inimigo.hpMaximo} ({pct}%)
                        </span>
                      </div>
                      <div className="w-full h-2 rounded-full bg-slate-900 overflow-hidden">
                        <div
                          className={`h-full transition-all duration-300 ${
                            derrotado
                              ? 'bg-emerald-500'
                              : pct < 50
                              ? 'bg-amber-500'
                              : 'bg-rose-500'
                          }`}
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        {/* Coluna Direita (5 cols): Nova Denúncia + Histórico do Usuário + Tabela de Multiplicadores */}
        <div className="lg:col-span-5 space-y-6">
          <ReportForm
            selectedCoords={selectedCoords}
            onSelectCoords={setSelectedCoords}
            selectedBairro={selectedBairro}
            onReportSuccess={() => loadDashboardData()}
          />

          {/* Histórico de Denúncias do Usuário (/api/v1/reports/mine) */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/90 p-5">
            <div className="flex items-center justify-between mb-4">
              <h2 className="font-display text-base font-bold text-white flex items-center gap-2">
                <History className="w-4 h-4 text-sky-400" />
                Meu Histórico de Combate ({reports.length})
              </h2>
              <span className="text-[11px] text-slate-400 font-mono">
                Denúncias enviadas por você
              </span>
            </div>

            {reports.length === 0 ? (
              <div className="text-center py-8 px-4 rounded-xl bg-slate-950/60 border border-slate-800/80 text-xs text-slate-400">
                Nenhuma denúncia enviada ainda. Escolha um bairro no mapa e envie sua primeira foto para causar dano ao Boss!
              </div>
            ) : (
              <div className="space-y-2.5 max-h-80 overflow-y-auto pr-1">
                {reports.map((rep, idx) => {
                  const catInfo =
                    CATEGORIAS_LIXO[rep.categoria] || CATEGORIAS_LIXO.NAO_IDENTIFICADO;
                  return (
                    <div
                      key={idx}
                      onClick={() => {
                        if (rep.latitude && rep.longitude) {
                          setSelectedCoords({
                            latitude: rep.latitude,
                            longitude: rep.longitude,
                          });
                        }
                      }}
                      className="p-3.5 rounded-xl bg-slate-950/80 hover:bg-slate-950 border border-slate-800/90 cursor-pointer transition space-y-1.5"
                    >
                      <div className="flex items-center justify-between gap-2">
                        <div className="flex items-center gap-2">
                          {rep.valido ? (
                            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                          ) : (
                            <XCircle className="w-4 h-4 text-rose-400 shrink-0" />
                          )}
                          <span
                            className={`text-[11px] font-bold px-2 py-0.5 rounded-md border ${catInfo.badge}`}
                          >
                            {catInfo.icon} {catInfo.label}
                          </span>
                          <span className="text-[11px] font-mono text-slate-400">
                            Sev. {rep.severidade ?? 0}/5
                          </span>
                        </div>
                        <span
                          className={`font-mono text-xs font-bold ${
                            rep.valido ? 'text-emerald-400' : 'text-slate-500'
                          }`}
                        >
                          {rep.valido ? `+${rep.dano} pontos` : 'Em análise'}
                        </span>
                      </div>

                      <p className="text-xs text-slate-300 line-clamp-2">
                        {rep.justificativaIa}
                      </p>

                      <div className="flex items-center justify-between text-[11px] text-slate-500 pt-1">
                        <span>
                          {rep.inimigo
                            ? `Alvo: ${rep.inimigo.nome} (${rep.inimigo.bairro})`
                            : 'Sem Boss associado'}
                        </span>
                        <span className="font-mono">
                          {rep.latitude?.toFixed(4)}, {rep.longitude?.toFixed(4)}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

        </div>
      </main>
    </div>
  );
}
