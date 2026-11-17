import React, { useState } from 'react';
import {
  Shield,
  LogIn,
  UserPlus,
  Sparkles,
  Server,
  AlertCircle,
  CheckCircle2,
  Play,
} from 'lucide-react';
import { api, getApiBaseUrl, setApiBaseUrl, setDemoMode } from '../services/api';

export default function AuthScreen({ onAuthenticated }) {
  const [tab, setTab] = useState('login'); // 'login' | 'register'
  const [login, setLogin] = useState('teste');
  const [password, setPassword] = useState('');
  const [nome, setNome] = useState('');
  const [apiUrl, setApiUrl] = useState(getApiBaseUrl());
  const [showConfig, setShowConfig] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');
    setLoading(true);
    setDemoMode(false);
    setApiBaseUrl(apiUrl);

    try {
      if (tab === 'login') {
        await api.login(login.trim(), password);
        onAuthenticated();
      } else {
        const res = await api.register(nome.trim(), login.trim(), password);
        setSuccessMsg(res?.mensagem || 'Conta criada com sucesso! Faça login para entrar.');
        setTab('login');
      }
    } catch (err) {
      setError(
        err.message === 'Failed to fetch'
          ? 'Não foi possível conectar ao serviço agora. Tente novamente ou entre no modo de demonstração.'
          : err.message || 'Falha na autenticação. Verifique suas credenciais.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleEnterDemo = async () => {
    setError('');
    setDemoMode(true);
    await api.login('teste', 'demo');
    onAuthenticated();
  };

  return (
    <div className="auth-shell min-h-screen bg-slate-50 text-slate-900 flex flex-col justify-center items-center p-4 relative overflow-hidden">
      {/* Glow decorativo */}
      <div className="absolute -top-40 -left-40 w-96 h-96 bg-emerald-100 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -bottom-40 -right-40 w-96 h-96 bg-amber-100 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md z-10">
        {/* Cabeçalho / Logo */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs font-semibold uppercase tracking-wider mb-4">
            <Sparkles className="w-3.5 h-3.5" />
            Prefeitura de Canoas • RPG Ambiental com IA
          </div>
          <h1 className="font-display text-4xl font-bold tracking-tight text-slate-900 flex items-center justify-center gap-3">
            <span className="p-2.5 rounded-xl bg-emerald-600 text-white shadow-sm">
              <Shield className="w-8 h-8" />
            </span>
            TRASH BUSTERS
          </h1>
          <p className="text-slate-400 text-sm mt-2">
            Denuncie descartes irregulares em Canoas, valide com IA multimodal e derrote os Bosses de cada bairro!
          </p>
        </div>

        {/* Card de Login / Registro */}
        <div className="bg-white border border-slate-200/80 rounded-xl p-6 shadow-sm">
          {/* Seletor de Abas */}
          <div className="grid grid-cols-2 gap-2 p-1 bg-slate-950 rounded-xl border border-slate-800 mb-6">
            <button
              type="button"
              onClick={() => {
                setTab('login');
                setError('');
                setSuccessMsg('');
              }}
              className={`flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-semibold transition ${
                tab === 'login'
                  ? 'bg-emerald-500 text-slate-950 shadow'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <LogIn className="w-4 h-4" />
              Entrar
            </button>
            <button
              type="button"
              onClick={() => {
                setTab('register');
                setError('');
                setSuccessMsg('');
              }}
              className={`flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-semibold transition ${
                tab === 'register'
                  ? 'bg-emerald-500 text-slate-950 shadow'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <UserPlus className="w-4 h-4" />
              Criar Conta
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-200 text-xs flex items-start gap-2.5">
              <AlertCircle className="w-4 h-4 text-rose-400 shrink-0 mt-0.5" />
              <span>{error}</span>
            </div>
          )}

          {successMsg && (
            <div className="mb-4 p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-200 text-xs flex items-start gap-2.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
              <span>{successMsg}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {tab === 'register' && (
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                  Nome do Agente
                </label>
                <input
                  type="text"
                  required
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  placeholder="Ex: Maria da Silva"
                  className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:outline-none text-sm text-white"
                />
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Login de Usuário
              </label>
              <input
                type="text"
                required
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                placeholder="Ex: cidadao_canoas"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:outline-none text-sm text-white"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Senha
              </label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:outline-none text-sm text-white"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm shadow-sm transition disabled:opacity-50"
            >
              {loading
                ? 'Conectando ao servidor...'
                : tab === 'login'
                ? 'Entrar na minha conta'
                : 'Registrar Novo Agente'}
            </button>
          </form>

          {/* Divisor Modo Demo */}
          <div className="my-5 flex items-center gap-3">
            <div className="h-px bg-slate-800 flex-1" />
            <span className="text-[11px] uppercase tracking-wider text-slate-500 font-semibold">
              Ou teste imediato
            </span>
            <div className="h-px bg-slate-800 flex-1" />
          </div>

          <button
            type="button"
            onClick={handleEnterDemo}
            className="w-full py-2.5 px-4 rounded-xl bg-slate-800/90 hover:bg-slate-800 border border-slate-700 text-slate-200 text-xs font-semibold flex items-center justify-center gap-2 transition"
          >
            <Play className="w-3.5 h-3.5 text-emerald-400" />
            Conhecer a plataforma em modo de demonstração
          </button>
        </div>
      </div>
    </div>
  );
}
