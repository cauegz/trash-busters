import React, { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import { BAIRROS_PRESETS, CANOAS_CENTER, getBairroMeta } from '../data/canoasBairros';
import { Crosshair, Layers, ShieldAlert, ShieldCheck } from 'lucide-react';

function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function hasCoordinatePair(coords) {
  return (
    coords?.latitude !== null &&
    coords?.latitude !== undefined &&
    coords?.longitude !== null &&
    coords?.longitude !== undefined &&
    Number.isFinite(Number(coords.latitude)) &&
    Number.isFinite(Number(coords.longitude))
  );
}

export default function CanoasMap({
  bairros = [],
  selectedBairro,
  onSelectBairro,
  selectedCoords,
  onSelectCoords,
  reports = [],
}) {
  const mapContainerRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const geoJsonLayerRef = useRef(null);
  const markersLayerRef = useRef(null);
  const pinMarkerRef = useRef(null);
  const [geoJsonData, setGeoJsonData] = useState(null);
  const [geoJsonError, setGeoJsonError] = useState('');
  const [showReportPins, setShowReportPins] = useState(true);

  // Carrega o GeoJSON oficial dos 18 bairros de Canoas
  useEffect(() => {
    fetch('/bairros.geojson')
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        if (!data || data.type !== 'FeatureCollection') {
          throw new Error('Formato GeoJSON inválido');
        }
        setGeoJsonData(data);
        setGeoJsonError('');
      })
      .catch(() => {
        setGeoJsonError(
          'Não foi possível carregar os limites dos bairros. Os marcadores continuam disponíveis.'
        );
      });
  }, []);

  // Inicializa o mapa Leaflet
  useEffect(() => {
    if (!mapContainerRef.current || mapInstanceRef.current) return;

    const map = L.map(mapContainerRef.current, {
      center: CANOAS_CENTER,
      zoom: 12,
      zoomControl: true,
    });

    const cartoApiKey = import.meta.env.VITE_CARTO_API_KEY?.trim();
    const tileUrl = cartoApiKey
      ? `https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png?key=${encodeURIComponent(cartoApiKey)}`
      : 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    const attribution = cartoApiKey
      ? '&copy; OpenStreetMap &copy; CARTO | Trash Busters Canoas'
      : '&copy; OpenStreetMap contributors | Trash Busters Canoas';

    L.tileLayer(tileUrl, {
      attribution,
      maxZoom: 19,
    }).addTo(map);

    markersLayerRef.current = L.layerGroup().addTo(map);

    map.on('click', (e) => {
      const { lat, lng } = e.latlng;
      if (onSelectCoords) {
        onSelectCoords({
          latitude: Number(lat.toFixed(6)),
          longitude: Number(lng.toFixed(6)),
        });
      }
    });

    mapInstanceRef.current = map;

    return () => {
      map.remove();
      mapInstanceRef.current = null;
    };
  }, []);

  // Renderiza polígonos dos bairros e marcadores dos Bosses
  useEffect(() => {
    const map = mapInstanceRef.current;
    if (!map) return;

    if (geoJsonLayerRef.current) {
      geoJsonLayerRef.current.remove();
      geoJsonLayerRef.current = null;
    }

    if (geoJsonData) {
      geoJsonLayerRef.current = L.geoJSON(geoJsonData, {
        style: (feature) => {
          const nome = feature?.properties?.name || '';
          const bairroObj = bairros.find(
            (b) => b.nome?.toLowerCase() === nome.toLowerCase()
          );
          const inimigo = bairroObj?.inimigo;
          const derrotado = inimigo?.statusInimigo === 'DERROTADO' || inimigo?.hpAtual === 0;
          const isSelected =
            selectedBairro &&
            selectedBairro.nome?.toLowerCase() === nome.toLowerCase();

          const hpRatio = inimigo
            ? Math.max(0, Math.min(1, inimigo.hpAtual / (inimigo.hpMaximo || 1000)))
            : 1;

          let fillColor = '#fb7185'; // rose-400 (Boss ativo HP alto)
          if (derrotado) {
            fillColor = '#34d399'; // emerald-400 (Bairro limpo / Boss derrotado)
          } else if (hpRatio < 0.5) {
            fillColor = '#fbbf24'; // amber-400 (Boss enfraquecido)
          }

          return {
            color: isSelected ? '#38bdf8' : fillColor,
            weight: isSelected ? 3 : 1.5,
            fillColor,
            fillOpacity: isSelected ? 0.35 : 0.18,
          };
        },
        onEachFeature: (feature, layer) => {
          const nome = feature?.properties?.name || 'Bairro';
          layer.on('click', (e) => {
            const bairroObj = bairros.find(
              (b) => b.nome?.toLowerCase() === nome.toLowerCase()
            );
            if (bairroObj && onSelectBairro) {
              onSelectBairro(bairroObj);
            }
            if (onSelectCoords && e.latlng) {
              onSelectCoords({
                latitude: Number(e.latlng.lat.toFixed(6)),
                longitude: Number(e.latlng.lng.toFixed(6)),
                bairroNome: nome,
              });
            }
          });
        },
      }).addTo(map);
    }

    // Atualiza marcadores dos Bosses e denúncias
    if (markersLayerRef.current) {
      markersLayerRef.current.clearLayers();

      bairros.forEach((b) => {
        const preset = getBairroMeta(b.nome);
        if (!preset?.lat || !preset?.lng) return;

        const inimigo = b.inimigo || {};
        const derrotado =
          inimigo.statusInimigo === 'DERROTADO' || inimigo.hpAtual === 0;
        const pct = Math.round(
          ((inimigo.hpAtual ?? 1000) / (inimigo.hpMaximo || 1000)) * 100
        );

        const iconHtml = `
          <div style="
            display:flex;align-items:center;gap:4px;
            background:${derrotado ? 'rgba(6,78,59,0.92)' : 'rgba(15,23,42,0.92)'};
            border:1.5px solid ${derrotado ? '#10b981' : '#f43f5e'};
            border-radius:999px;padding:2px 8px;
            color:#f8fafc;font-size:11px;font-weight:700;
            box-shadow:0 4px 12px rgba(0,0,0,0.5);white-space:nowrap;
          ">
            <span>${derrotado ? '🏆' : preset.emoji}</span>
            <span>${escapeHtml(b.nome)}</span>
            <span style="color:${derrotado ? '#34d399' : '#fda4af'};font-family:monospace;">
              ${derrotado ? 'LIMPO' : `${pct}%`}
            </span>
          </div>
        `;

        const icon = L.divIcon({
          html: iconHtml,
          className: 'custom-boss-marker',
          iconSize: [110, 26],
          iconAnchor: [55, 13],
        });

        const marker = L.marker([preset.lat, preset.lng], { icon });
        marker.on('click', () => {
          if (onSelectBairro) onSelectBairro(b);
          if (onSelectCoords) {
            onSelectCoords({
              latitude: preset.lat,
              longitude: preset.lng,
              endereco: preset.enderecoExemplo,
              bairroNome: b.nome,
            });
          }
        });

        marker.addTo(markersLayerRef.current);
      });

      if (showReportPins && Array.isArray(reports)) {
        reports.forEach((rep) => {
          if (rep.latitude == null || rep.longitude == null) return;
          const circle = L.circleMarker([rep.latitude, rep.longitude], {
            radius: 6,
            fillColor: rep.valido ? '#38bdf8' : '#94a3b8',
            color: '#0f172a',
            weight: 2,
            fillOpacity: 0.9,
          });
          circle.bindPopup(`
            <div style="font-size:12px;min-width:170px;">
              <div style="font-weight:700;margin-bottom:4px;color:${rep.valido ? '#34d399' : '#f87171'}">
                ${rep.valido ? `✅ Denúncia confirmada (${escapeHtml(rep.categoria)})` : '❌ Não confirmada'}
              </div>
              <div style="color:#cbd5e1;font-size:11px;">${escapeHtml(rep.justificativaIa)}</div>
            </div>
          `);
          circle.addTo(markersLayerRef.current);
        });
      }
    }
  }, [geoJsonData, bairros, selectedBairro, reports, showReportPins]);

  // Atualiza o marcador do pino selecionado para nova denúncia
  useEffect(() => {
    const map = mapInstanceRef.current;
    if (!map) return;

    if (pinMarkerRef.current) {
      pinMarkerRef.current.remove();
      pinMarkerRef.current = null;
    }

    if (hasCoordinatePair(selectedCoords)) {
      const pinIcon = L.divIcon({
        html: `
          <div style="
            width:28px;height:28px;border-radius:50%;
            background:rgba(16,185,129,0.25);border:2px solid #10b981;
            display:flex;align-items:center;justify-content:center;
            box-shadow:0 0 15px rgba(16,185,129,0.8);
          ">
            <div style="width:10px;height:10px;border-radius:50%;background:#10b981;"></div>
          </div>
        `,
        className: 'target-pin-icon',
        iconSize: [28, 28],
        iconAnchor: [14, 14],
      });

      pinMarkerRef.current = L.marker(
        [selectedCoords.latitude, selectedCoords.longitude],
        { icon: pinIcon }
      )
        .addTo(map)
        .bindPopup(
          `<div style="font-size:12px"><b>Alvo Selecionado</b><br/>Lat: ${selectedCoords.latitude}<br/>Lng: ${selectedCoords.longitude}</div>`
        );
    }
  }, [selectedCoords]);

  const handleCenterCanoas = () => {
    if (mapInstanceRef.current) {
      mapInstanceRef.current.setView(CANOAS_CENTER, 12);
    }
  };

  return (
    <div className="map-card relative overflow-hidden">
      {/* Barra superior do mapa */}
      <div className="flex flex-wrap items-center justify-between gap-2 px-4 py-3 bg-white border-b border-slate-200">
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center justify-center w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
          <h3 className="font-display font-bold text-sm sm:text-base text-slate-900">
            Mapa de ocorrências em Canoas/RS
          </h3>
        </div>

        <div className="flex items-center gap-2 text-xs">
          <button
            type="button"
            onClick={() => setShowReportPins((v) => !v)}
            className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg border transition ${
              showReportPins
                ? 'bg-sky-50 text-sky-700 border-sky-300'
                : 'bg-slate-100 text-slate-600 border-slate-300'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            Minhas Denúncias ({reports.length})
          </button>
          <button
            type="button"
            onClick={handleCenterCanoas}
            className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-300 transition"
          >
            <Crosshair className="w-3.5 h-3.5" />
            Centralizar Canoas
          </button>
        </div>
      </div>

      {geoJsonError && (
        <div className="px-4 py-2.5 bg-amber-50 border-b border-amber-200 text-xs text-amber-800">
          {geoJsonError}
        </div>
      )}

      {/* Container Leaflet */}
      <div ref={mapContainerRef} className="w-full h-[380px] sm:h-[440px] z-0" />

      {/* Legenda inferior */}
      <div className="flex flex-wrap items-center justify-between gap-3 px-4 py-3 bg-slate-50 border-t border-slate-200 text-xs text-slate-600">
        <div className="flex flex-wrap items-center gap-4">
          <span className="inline-flex items-center gap-1.5">
            <span className="w-3 h-3 rounded-sm bg-rose-500/70 border border-rose-400" />
            Área com mais ocorrências
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-3 h-3 rounded-sm bg-amber-500/70 border border-amber-400" />
            Área em recuperação
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-3 h-3 rounded-sm bg-emerald-500/70 border border-emerald-400" />
            Bairro Limpo (Boss Derrotado)
          </span>
        </div>
        <span className="text-slate-400">
          💡 Clique em um bairro ou ponto do mapa para escolher o local da denúncia
        </span>
      </div>
    </div>
  );
}
