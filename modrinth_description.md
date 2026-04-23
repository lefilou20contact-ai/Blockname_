# 🔍 BlockName

> **Scannez chaque bloc du monde — nom, outil, dureté, Silk Touch, Fortune et plus encore.**

---

## ✨ Qu'est-ce que BlockName ?

**BlockName** est un mod client Fabric qui affiche en temps réel toutes les informations utiles sur le bloc que vous regardez.
Plus besoin de chercher sur le wiki : tout est affiché directement dans votre HUD, avec une interface élégante en verre.

---

## 🟦 Fonctionnalités

### 🔎 Mode Scan
- Nom complet du bloc
- Outil exact requis (Pioche en fer, Hache en diamant, etc.)
- Indication Silk Touch si nécessaire
- Indication Fortune si applicable
- Dureté & résistance aux explosions
- Activable/désactivable avec une touche configurable (défaut : **F9**)
- **Désactivé automatiquement en multijoueur** (solo uniquement)

### 🖥️ Interface en Verre
- Fond translucide avec coins arrondis
- **Thème sombre / thème clair**
- **Mode compact** (toast minimaliste) ou **mode large** (toutes les infos)
- Animations d'apparition/disparition fluides

### 🐛 Mode Debug (F10)
- ID complet du bloc (`minecraft:stone`)
- Tags Minecraft complets
- Loot table
- Propriétés du bloc (waterlogged, age, facing…)
- Hitbox visible

### ⚙️ Configuration complète
- Fichier `config/blockname.json` entièrement personnalisable
- Intégration **ModMenu** avec écran de configuration dédié
- Toutes les options activables/désactivables indépendamment

### ⚡ Performance
- Cache intelligent (LRU) pour zéro surcharge CPU
- Tick handler optimisé
- Aucun impact sur les performances du jeu

### 🌍 Multi-Version
Compatible avec :
- Minecraft **1.20.1**
- Minecraft **1.20.2 → 1.20.6**
- Minecraft **1.21 → 1.21.1**

### 🌐 Langues incluses
- 🇫🇷 Français (`fr_fr`)
- 🇬🇧 Anglais (`en_us`)

---

## 📦 Installation

1. Installer **Fabric Loader** pour votre version de Minecraft
2. Installer **Fabric API**
3. Télécharger **BlockName** et placer le `.jar` dans votre dossier `mods/`
4. (Optionnel) Installer **ModMenu** pour accéder aux paramètres depuis le menu

---

## ⌨️ Raccourcis clavier

| Touche | Action |
|--------|--------|
| `F9` | Activer / désactiver le Scan |
| `F10` | Activer / désactiver le Debug |

*(Configurables dans les paramètres de touches de Minecraft)*

---

## 🔧 Configuration

Le fichier `config/blockname.json` est créé automatiquement au premier lancement.

```json
{
  "scan": {
    "enabled": true,
    "show_tool_required": true,
    "show_silk_touch": true,
    "show_fortune": true,
    "show_hardness": true,
    "multiplayer_disabled": true
  },
  "ui": {
    "theme": "dark",
    "mode": "large",
    "animations": true
  }
}
```

---

## 🤝 Dépendances

| Mod | Obligatoire |
|-----|-------------|
| Fabric Loader ≥ 0.14 | ✅ Oui |
| Fabric API | ✅ Oui |
| ModMenu | ⭐ Recommandé |

---

## 📸 Captures d'écran

*[Ajouter vos screenshots ici]*

---

## 📄 Licence

MIT — Libre d'utilisation et de modification.

---

*Fait avec ❤️ par Dominique*
