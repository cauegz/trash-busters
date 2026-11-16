import React, { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import { BAIRROS_PRESETS, CANOAS_CENTER, getBairroMeta } from '../data/canoasBairros';
import { Crosshair, Layers, ShieldAlert, ShieldCheck } from 'lucide-react';

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
  const [showReportPins, setShowReportPins] = useState(true);

  // Carrega o GeoJSON oficial dos 18 bairros de Canoas
  useEffect(() => {
    fetch('/bairros.geojson')
      .then((r) => (r.ok ? r.json() : null))
      .then((data) => {
        if (data) setGeoJsonData(data);
      })
      .catch(() => {});
  }, []);

  // Inicializa o mapa Leaflet
  useEffect(() => {
    if (!mapContainerRef.current || mapInstanceRef.current) return;

    const map = L.map(mapContainerRef.current, {
      center: CANOAS_CENTER,
      zoom: 12,
      zoomControl: true,
    });

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; OpenStreetMap &copy; CARTO | Trash Busters Canoas',
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

          let fillColor = '#f43f5e'; // rose-500 (Boss ativo HP alto)
          if (derrotado) {
            fillColor = '#10b981'; // emerald-500 (Bairro limpo / Boss derrotado)
          } else if (hpRatio < 0.5) {
            fillColor = '#f59e0b'; // amber-500 (Boss enfraquecido)
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
            <span>${b.nome}</span>
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
                ${rep.valido ? `💥 -${rep.dano} HP (${rep.categoria})` : '❌ Rejeitado pela IA'}
              </div>
              <div style="color:#cbd5e1;font-size:11px;">${rep.justificativaIa || ''}</div>
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

    if (selectedCoords?.latitude && selectedCoords?.longitude) {
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
    <div className="relative rounded-2xl overflow-hidden border border-slate-800 bg-slate-900/70 shadow-2xl">
      {/* Barra superior do mapa */}
      <div className="flex flex-wrap items-center justify-between gap-2 px-4 py-3 bg-slate-900/95 border-b border-slate-800">
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center justify-center w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
          <h3 className="font-display font-bold text-sm sm:text-base text-slate-100">
            Radar Geoespacial PostGIS — Bairros de Canoas/RS
          </h3>
        </div>

        <div className="flex items-center gap-2 text-xs">
          <button
            type="button"
            onClick={() => setShowReportPins((v) => !v)}
            className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg border transition ${
              showReportPins
                ? 'bg-sky-500/15 text-sky-300 border-sky-500/40'
                : 'bg-slate-800 text-slate-400 border-slate-700'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            Minhas Denúncias ({reports.length})
          </button>
          <button
            type="button"
            onClick={handleCenterCanoas}
            className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition"
          >
            <Crosshair className="w-3.5 h-3.5" />
            Centralizar Canoas
          </button>
        </div>
      </div>

      {/* Container Leaflet */}
      <div ref={mapContainerRef} className="w-full h-[380px] sm:h-[440px] z-0" />

      {/* Legenda inferior */}
      <div className="flex flex-wrap items-center justify-between gap-3 px-4 py-2.5 bg-slate-950/90 border-t border-slate-800/80 text-xs text-slate-400">
        <div className="flex flex-wrap items-center gap-4">
          <span className="inline-flex items-center gap-1.5">
            <span className="w-3 h-3 rounded-sm bg-rose-500/70 border border-rose-400" />
            Boss Ativo (HP Alto)
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-3 h-3 rounded-sm bg-amber-500/70 border border-amber-400" />
            Boss Enfraquecido (&lt;50% HP)
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-3 h-3 rounded-sm bg-emerald-500/70 border border-emerald-400" />
            Bairro Limpo (Boss Derrotado)
          </span>
        </div>
        <span className="text-slate-400">
          💡 Clique em qualquer bairro no mapa para mirar o ataque naquela coordenada
        </span>
      </div>
    </div>
  );
}
