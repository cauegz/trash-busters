// Coordenadas reais dentro dos polígonos dos 18 bairros de Canoas/RS (SRID 4326)
// e metadados visuais para os Bosses de cada bairro.

export const CANOAS_CENTER = [-29.9128, -51.1815];

export const CATEGORIAS_LIXO = {
  HOSPITALAR: { label: 'Hospitalar', mult: 2.5, badge: 'bg-rose-500/20 text-rose-300 border-rose-500/40', icon: '☣️' },
  ELETRONICO: { label: 'Eletrônico (E-Lixo)', mult: 2.0, badge: 'bg-purple-500/20 text-purple-300 border-purple-500/40', icon: '🔌' },
  ENTULHO: { label: 'Entulho / Obra', mult: 1.8, badge: 'bg-amber-500/20 text-amber-300 border-amber-500/40', icon: '🧱' },
  VIDRO: { label: 'Vidro', mult: 1.4, badge: 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40', icon: '🍾' },
  METAL: { label: 'Metal / Sucata', mult: 1.3, badge: 'bg-slate-400/20 text-slate-200 border-slate-400/40', icon: '⚙️' },
  PLASTICO: { label: 'Plástico', mult: 1.2, badge: 'bg-blue-500/20 text-blue-300 border-blue-500/40', icon: '🧴' },
  PAPEL: { label: 'Papel / Papelão', mult: 1.1, badge: 'bg-yellow-500/20 text-yellow-300 border-yellow-500/40', icon: '📦' },
  ORGANICO: { label: 'Orgânico', mult: 1.0, badge: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40', icon: '🍂' },
  REJEITO: { label: 'Rejeito Comum', mult: 1.0, badge: 'bg-orange-500/20 text-orange-300 border-orange-500/40', icon: '🗑️' },
  NAO_IDENTIFICADO: { label: 'Não Identificado', mult: 1.0, badge: 'bg-slate-600/20 text-slate-300 border-slate-500/40', icon: '❓' },
};

export const BAIRROS_PRESETS = [
  { id: 1, nome: 'Mathias Velho', lat: -29.8950, lng: -51.1930, enderecoExemplo: 'Av. Rio Grande do Sul, 1500 - Mathias Velho', bossDefault: 'EntulhoSauro', hpMax: 1500, emoji: '🦖', titulo: 'Colosso de Concreto e Caliça' },
  { id: 2, nome: 'Guajuviras', lat: -29.9035, lng: -51.1405, enderecoExemplo: 'Av. 17 de Abril, 420 - Guajuviras', bossDefault: 'Monstro do Plástico', hpMax: 2000, emoji: '👾', titulo: 'Hidra de Polímeros Descartados' },
  { id: 3, nome: 'Niterói', lat: -29.9510, lng: -51.1680, enderecoExemplo: 'Rua Júlio de Castilhos, 810 - Niterói', bossDefault: 'Kraken dos Entulhos', hpMax: 1200, emoji: '🐙', titulo: 'Tentáculos de Descarte Irregular' },
  { id: 4, nome: 'Centro', lat: -29.9189, lng: -51.1836, enderecoExemplo: 'Rua Tiradentes, 300 - Centro', bossDefault: 'Lord Pneu', hpMax: 1000, emoji: '🛞', titulo: 'Barão da Borracha Tóxica' },
  { id: 5, nome: 'Harmonia', lat: -29.9180, lng: -51.2015, enderecoExemplo: 'Rua República, 950 - Harmonia', bossDefault: 'Fumaça Negra', hpMax: 1300, emoji: '🌫️', titulo: 'Espectro de Fuligem Urbana' },
  { id: 6, nome: 'Marechal Rondon', lat: -29.9165, lng: -51.1705, enderecoExemplo: 'Av. Inconfidência, 600 - Marechal Rondon', bossDefault: 'Boss de Marechal Rondon', hpMax: 1000, emoji: '🤖', titulo: 'Golem de Sucata Urbana' },
  { id: 7, nome: 'Nossa Senhora das Graças', lat: -29.9335, lng: -51.1710, enderecoExemplo: 'Rua Santos Ferreira, 1100 - N. Sra. das Graças', bossDefault: 'Boss de Nossa Senhora das Graças', hpMax: 1000, emoji: '🦠', titulo: 'Parasita de Resíduos' },
  { id: 8, nome: 'Igara', lat: -29.8955, lng: -51.1630, enderecoExemplo: 'Av. Boqueirão, 2400 - Igara', bossDefault: 'Boss de Igara', hpMax: 1000, emoji: '🦂', titulo: 'Escorpião de Sucata Eletrônica' },
  { id: 9, nome: 'Estância Velha', lat: -29.9160, lng: -51.1500, enderecoExemplo: 'Rua Santos Ferreira, 3200 - Estância Velha', bossDefault: 'Boss de Estância Velha', hpMax: 1000, emoji: '🐉', titulo: 'Dragão de Sacolas Plásticas' },
  { id: 10, nome: 'Olaria', lat: -29.9320, lng: -51.1480, enderecoExemplo: 'Av. do Nazário, 1200 - Olaria', bossDefault: 'Boss de Olaria', hpMax: 1000, emoji: '🧱', titulo: 'Titã de Cerâmica Quebrada' },
  { id: 11, nome: 'Fátima', lat: -29.9465, lng: -51.1865, enderecoExemplo: 'Rua Bartolomeu de Gusmão, 450 - Fátima', bossDefault: 'Boss de Fátima', hpMax: 1000, emoji: '🕷️', titulo: 'Aracnídeo de Fios e Cabos' },
  { id: 12, nome: 'Rio Branco', lat: -29.9610, lng: -51.1920, enderecoExemplo: 'Rua Boa Saúde, 1500 - Rio Branco', bossDefault: 'Boss de Rio Branco', hpMax: 1000, emoji: '🐊', titulo: 'Leviatã da Vala Poluída' },
  { id: 13, nome: 'Mato Grande', lat: -29.9030, lng: -51.2150, enderecoExemplo: 'Rua da República, 2800 - Mato Grande', bossDefault: 'Boss de Mato Grande', hpMax: 1000, emoji: '🐗', titulo: 'Fera do Aterro Clandestino' },
  { id: 14, nome: 'São José', lat: -29.8780, lng: -51.1650, enderecoExemplo: 'Av. Farroupilha, 4500 - São José', bossDefault: 'Boss de São José', hpMax: 1000, emoji: '🦇', titulo: 'Sombra do Descarte Noturno' },
  { id: 15, nome: 'Brigadeira', lat: -29.8785, lng: -51.1860, enderecoExemplo: 'Av. Açucena, 700 - Brigadeira', bossDefault: 'Boss de Brigadeira', hpMax: 1000, emoji: '🦎', titulo: 'Salamandra de Óleo e Graxa' },
  { id: 16, nome: 'Industrial', lat: -29.8920, lng: -51.1790, enderecoExemplo: 'Av. Guilherme Schell, 7800 - Industrial', bossDefault: 'Boss de Industrial', hpMax: 1000, emoji: '🏭', titulo: 'Autômato de Escória Industrial' },
  { id: 17, nome: 'São Luís', lat: -29.8820, lng: -51.1780, enderecoExemplo: 'Rua Berto Círio, 900 - São Luís', bossDefault: 'Boss de São Luís', hpMax: 1000, emoji: '🦀', titulo: 'Caranguejo de Latas Amassadas' },
  { id: 18, nome: 'Ilha das Garças', lat: -29.9680, lng: -51.2220, enderecoExemplo: 'Estrada da Ilha das Garças, 200 - Ilha das Garças', bossDefault: 'Boss de Ilha das Garças', hpMax: 1000, emoji: '🐍', titulo: 'Serpente de Redes e Garrafas' },
];

export function getBairroMeta(nomeBairro) {
  if (!nomeBairro) return { emoji: '👾', titulo: 'Chefe do Descarte Irregular', lat: -29.9189, lng: -51.1836 };
  const found = BAIRROS_PRESETS.find(
    (b) => b.nome.toLowerCase() === nomeBairro.toLowerCase()
  );
  return found || { emoji: '👾', titulo: `Ameaça Ambiental de ${nomeBairro}`, lat: -29.9189, lng: -51.1836 };
}

export function getPlayerRank(xp = 0) {
  if (xp >= 1500) return { level: Math.floor(xp / 150) + 1, title: 'Lenda Eco-Guardião', color: 'text-amber-400', nextXp: 2000 };
  if (xp >= 800) return { level: Math.floor(xp / 150) + 1, title: 'Comandante Ambiental', color: 'text-purple-400', nextXp: 1500 };
  if (xp >= 400) return { level: Math.floor(xp / 150) + 1, title: 'Caçador de Entulhos', color: 'text-cyan-400', nextXp: 800 };
  if (xp >= 150) return { level: Math.floor(xp / 150) + 1, title: 'Patrulheiro de Bairro', color: 'text-emerald-400', nextXp: 400 };
  return { level: 1, title: 'Recruta Trash Buster', color: 'text-slate-300', nextXp: 150 };
}
