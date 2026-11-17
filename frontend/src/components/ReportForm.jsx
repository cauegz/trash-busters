import React, { useState, useEffect } from 'react';
import {
  Camera,
  MapPin,
  Send,
  Sparkles,
  Crosshair,
  AlertTriangle,
  CheckCircle2,
  ShieldAlert,
  Flame,
  Wand2,
} from 'lucide-react';
import { BAIRROS_PRESETS, CATEGORIAS_LIXO } from '../data/canoasBairros';
import { api, isDemoMode } from '../services/api';

// Gera uma imagem sintética em Canvas para facilitar testes rápidos sem precisar procurar arquivo no PC
function generateSampleTrashImage(bairroNome = 'Canoas', categoria = 'ENTULHO') {
  return new Promise((resolve) => {
    const canvas = document.createElement('canvas');
    canvas.width = 640;
    canvas.height = 420;
    const ctx = canvas.getContext('2d');

    // Fundo rua/calçada
    const grad = ctx.createLinearGradient(0, 0, 640, 420);
    grad.addColorStop(0, '#1e293b');
    grad.addColorStop(1, '#0f172a');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, 640, 420);

    // Calçada / entulho ilustrativo
    ctx.fillStyle = '#334155';
    ctx.fillRect(0, 290, 640, 130);

    // Sacos de lixo e caixas simulados
    ctx.fillStyle = '#475569';
    ctx.beginPath();
    ctx.arc(220, 285, 55, 0, Math.PI * 2);
    ctx.arc(300, 295, 65, 0, Math.PI * 2);
    ctx.arc(390, 290, 50, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = '#ef4444';
    ctx.fillRect(250, 260, 45, 30);
    ctx.fillStyle = '#3b82f6';
    ctx.fillRect(340, 275, 38, 24);
    ctx.fillStyle = '#eab308';
    ctx.fillRect(180, 280, 40, 28);

    // Texto de evidência
    ctx.fillStyle = '#10b981';
    ctx.font = 'bold 22px sans-serif';
    ctx.fillText(`EVIDÊNCIA DE DESCARTE - ${bairroNome.toUpperCase()}`, 32, 48);
    ctx.fillStyle = '#e2e8f0';
    ctx.font = '16px monospace';
    ctx.fillText(`Categoria estimada: ${categoria} | Canoas/RS`, 32, 80);

    canvas.toBlob(
      (blob) => {
        const file = new File([blob], `denuncia_${bairroNome.toLowerCase().replace(/\s+/g, '_')}.jpg`, {
          type: 'image/jpeg',
        });
        resolve({ file, previewUrl: URL.createObjectURL(blob) });
      },
      'image/jpeg',
      0.9
    );
  });
}

export default function ReportForm({
  selectedCoords,
  onSelectCoords,
  selectedBairro,
  onReportSuccess,
}) {
  const [endereco, setEndereco] = useState('Av. Rio Grande do Sul, 1500 - Mathias Velho');
  const [latitude, setLatitude] = useState(-29.8950);
  const [longitude, setLongitude] = useState(-51.1930);
  const [imagemFile, setImagemFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [demoCategoria, setDemoCategoria] = useState('ENTULHO');
  const [demoSeveridade, setDemoSeveridade] = useState(4);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [lastResult, setLastResult] = useState(null);

  // Sincroniza quando o usuário clica no mapa ou escolhe um bairro
  useEffect(() => {
    if (selectedCoords?.latitude && selectedCoords?.longitude) {
      setLatitude(selectedCoords.latitude);
      setLongitude(selectedCoords.longitude);
      if (selectedCoords.endereco) {
        setEndereco(selectedCoords.endereco);
      } else if (selectedCoords.bairroNome) {
        setEndereco(`Ponto selecionado no mapa - Bairro ${selectedCoords.bairroNome}, Canoas/RS`);
      }
    }
  }, [selectedCoords]);

  const handlePresetChange = (e) => {
    const presetName = e.target.value;
    const found = BAIRROS_PRESETS.find((b) => b.nome === presetName);
    if (found) {
      setLatitude(found.lat);
      setLongitude(found.lng);
      setEndereco(found.enderecoExemplo);
      if (onSelectCoords) {
        onSelectCoords({
          latitude: found.lat,
          longitude: found.lng,
          endereco: found.enderecoExemplo,
          bairroNome: found.nome,
        });
      }
    }
  };

  const handleUseBrowserGps = () => {
    if (!navigator.geolocation) {
      setError('Geolocalização não suportada pelo seu navegador.');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const lat = Number(pos.coords.latitude.toFixed(6));
        const lng = Number(pos.coords.longitude.toFixed(6));
        setLatitude(lat);
        setLongitude(lng);
        if (onSelectCoords) {
          onSelectCoords({ latitude: lat, longitude: lng });
        }
      },
      () => {
        setError('Não foi possível obter sua localização GPS. Use o seletor de bairro ou clique no mapa.');
      }
    );
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setImagemFile(file);
    setPreviewUrl(URL.createObjectURL(file));
    setError('');
  };

  const handleGenerateTestPhoto = async () => {
    const bName = selectedBairro?.nome || 'Mathias Velho';
    const { file, previewUrl: url } = await generateSampleTrashImage(bName, demoCategoria);
    setImagemFile(file);
    setPreviewUrl(url);
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLastResult(null);

    if (!imagemFile) {
      setError('Selecione uma foto do descarte irregular (ou clique em "Gerar Foto de Teste").');
      return;
    }

    setSubmitting(true);
    try {
      const result = await api.sendReport({
        endereco,
        latitude: Number(latitude),
        longitude: Number(longitude),
        imagem: imagemFile,
        demoCategoria,
        demoSeveridade: Number(demoSeveridade),
      });
      setLastResult(result);
      if (onReportSuccess) {
        onReportSuccess(result);
      }
    } catch (err) {
      setError(err.message || 'Erro ao processar denúncia.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="report-card">
      <div className="flex items-center justify-between gap-2 mb-4">
        <div>
          <span className="text-[11px] font-bold uppercase tracking-wider text-emerald-400 flex items-center gap-1.5">
            <Flame className="w-3.5 h-3.5" />
            Central de denúncias
          </span>
          <h2 className="font-display text-lg font-bold text-white">
            Registrar novo descarte irregular
          </h2>
        </div>
      </div>

      {/* Resultado da denúncia */}
      {lastResult && (
        <div
          className={`mb-5 p-4 rounded-xl border ${
            lastResult.valido
              ? 'bg-emerald-950/50 border-emerald-500/50 text-emerald-100'
              : 'bg-rose-950/50 border-rose-500/50 text-rose-100'
          }`}
        >
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-2 font-display font-bold text-base">
              {lastResult.valido ? (
                <>
                  <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
                  <span>Denúncia confirmada! Você ajudou a limpar a cidade.</span>
                </>
              ) : (
                <>
                  <ShieldAlert className="w-5 h-5 text-rose-400 shrink-0" />
                  <span>Não foi possível confirmar esta denúncia</span>
                </>
              )}
            </div>
            {lastResult.valido && (
              <span className="px-2.5 py-0.5 rounded-full text-xs font-mono font-bold bg-emerald-500 text-slate-950">
                +{lastResult.dano} pontos
              </span>
            )}
          </div>

          <p className="text-xs text-slate-300 mt-2 leading-relaxed">
            <strong>Resultado da análise:</strong> {lastResult.justificativaIa}
          </p>

          <div className="mt-3 flex flex-wrap items-center gap-2 text-xs">
            <span className="px-2.5 py-1 rounded-lg bg-slate-900/80 border border-slate-700 font-mono">
              Categoria: <strong>{lastResult.categoria || 'Não identificada'}</strong>
            </span>
            <span className="px-2.5 py-1 rounded-lg bg-slate-900/80 border border-slate-700 font-mono">
              Nível de impacto: <strong>{lastResult.severidade ?? 0}/5</strong>
            </span>
            {lastResult.inimigo && (
              <span className="px-2.5 py-1 rounded-lg bg-slate-900/80 border border-slate-700 font-mono text-amber-300">
                Área beneficiada: {lastResult.inimigo.bairro || lastResult.inimigo.nome}
              </span>
            )}
          </div>
        </div>
      )}

      {error && (
        <div className="mb-4 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-200 text-xs flex items-start gap-2">
          <AlertTriangle className="w-4 h-4 text-rose-400 shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Seletor rápido de Bairro de Canoas */}
        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-slate-400">
              1. Localização em Canoas (Bairro / Coordenadas)
            </label>
            <button
              type="button"
              onClick={handleUseBrowserGps}
              className="text-xs text-emerald-400 hover:text-emerald-300 inline-flex items-center gap-1 font-medium"
            >
              <Crosshair className="w-3.5 h-3.5" />
              Usar meu GPS
            </button>
          </div>

          <select
            onChange={handlePresetChange}
            value={
              BAIRROS_PRESETS.find(
                (b) =>
                  Math.abs(b.lat - latitude) < 0.002 &&
                  Math.abs(b.lng - longitude) < 0.002
              )?.nome || ''
            }
            className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:outline-none text-sm text-slate-100 mb-2.5"
          >
            <option value="" disabled>
              -- Escolha um Bairro de Canoas ou clique no Mapa --
            </option>
            {BAIRROS_PRESETS.map((b) => (
              <option key={b.id} value={b.nome}>
                {b.emoji} {b.nome} — Boss: {b.bossDefault}
              </option>
            ))}
          </select>

          <div className="grid grid-cols-2 gap-2.5 mb-2.5">
            <div>
              <label className="block text-[11px] text-slate-400 mb-1">
                Latitude (Y)
              </label>
              <input
                type="number"
                step="any"
                required
                value={latitude}
                onChange={(e) => setLatitude(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 font-mono text-xs text-slate-200"
              />
            </div>
            <div>
              <label className="block text-[11px] text-slate-400 mb-1">
                Longitude (X)
              </label>
              <input
                type="number"
                step="any"
                required
                value={longitude}
                onChange={(e) => setLongitude(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 font-mono text-xs text-slate-200"
              />
            </div>
          </div>

          <div>
            <label className="block text-[11px] text-slate-400 mb-1">
              Endereço ou Ponto de Referência
            </label>
            <div className="relative">
              <MapPin className="w-4 h-4 text-slate-500 absolute left-3 top-2.5" />
              <input
                type="text"
                required
                value={endereco}
                onChange={(e) => setEndereco(e.target.value)}
                placeholder="Ex: Rua Tiradentes, 300 - Centro"
                className="w-full pl-9 pr-3.5 py-2 rounded-xl bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:outline-none text-sm text-slate-100"
              />
            </div>
          </div>
        </div>

        {/* Upload de Imagem */}
        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-slate-400">
              2. Foto da Evidência (Máx 5MB)
            </label>
            <button
              type="button"
              onClick={handleGenerateTestPhoto}
              className="text-xs text-cyan-400 hover:text-cyan-300 inline-flex items-center gap-1 font-medium"
            >
              <Wand2 className="w-3.5 h-3.5" />
              Gerar Foto de Teste
            </button>
          </div>

          <label className="flex flex-col items-center justify-center w-full h-36 rounded-xl border-2 border-dashed border-slate-700 hover:border-emerald-500/60 bg-slate-950/60 cursor-pointer transition overflow-hidden relative">
            {previewUrl ? (
              <div className="w-full h-full relative group">
                <img
                  src={previewUrl}
                  alt="Preview da denúncia"
                  className="w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-slate-950/60 opacity-0 group-hover:opacity-100 flex items-center justify-center transition text-xs font-semibold text-white">
                  Clique para trocar a imagem ({imagemFile?.name})
                </div>
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center p-4 text-center">
                <Camera className="w-7 h-7 text-emerald-400 mb-1.5" />
                <span className="text-xs font-semibold text-slate-200">
                  Clique para enviar foto do lixo
                </span>
                <span className="text-[11px] text-slate-500 mt-0.5">
                  JPG, PNG ou WEBP • A foto será analisada automaticamente
                </span>
              </div>
            )}
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="hidden"
            />
          </label>
        </div>

        {/* Opções extras quando em Modo Simulação */}
        {isDemoMode() && (
          <div className="p-3 rounded-xl bg-slate-950/90 border border-slate-800 space-y-2.5">
            <div className="text-[11px] font-semibold uppercase tracking-wider text-amber-400 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5" />
              Preferências da demonstração
            </div>
            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="block text-[11px] text-slate-400 mb-1">
                  Tipo de descarte
                </label>
                <select
                  value={demoCategoria}
                  onChange={(e) => setDemoCategoria(e.target.value)}
                  className="w-full px-2.5 py-1.5 rounded-lg bg-slate-900 border border-slate-700 text-xs text-slate-200"
                >
                  {Object.entries(CATEGORIAS_LIXO).map(([key, info]) => (
                    <option key={key} value={key}>
                      {info.icon} {info.label} ({info.mult}x)
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-[11px] text-slate-400 mb-1">
                  Nível de impacto (1 a 5)
                </label>
                <select
                  value={demoSeveridade}
                  onChange={(e) => setDemoSeveridade(Number(e.target.value))}
                  className="w-full px-2.5 py-1.5 rounded-lg bg-slate-900 border border-slate-700 text-xs text-slate-200"
                >
                  {[1, 2, 3, 4, 5].map((n) => (
                    <option key={n} value={n}>
                      Nível {n}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        )}

        <button
          type="submit"
          disabled={submitting}
          className="w-full py-3 px-4 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-display font-bold text-sm shadow-sm flex items-center justify-center gap-2 transition disabled:opacity-50"
        >
          <Send className="w-4 h-4" />
          {submitting
            ? 'Analisando sua denúncia...'
            : 'Enviar denúncia'}
        </button>
      </form>
    </div>
  );
}
