# 🔍 BlockName — Mod Fabric

> Affiche le nom, l'outil requis, la dureté, Silk Touch, Fortune et plus pour chaque bloc pointé.

[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-blue)](https://fabricmc.net/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1%20→%201.21.1-green)](https://minecraft.net)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## ✨ Fonctionnalités

- 🔎 **Mode Scan** — infos complètes du bloc ciblé en temps réel
- 🖥️ **Interface en verre** — thème clair/sombre, mode compact/large, animations
- 🐛 **Mode Debug** — ID, tags, loot table, propriétés du bloc
- ⚙️ **Config JSON** — `config/blockname.json`
- 🎛️ **ModMenu** — écran de configuration in-game
- ⚡ **Cache LRU** — zéro surcharge CPU
- 🌐 **Multi-version** — 1.20.1 → 1.21.1

---

## 📦 Installation

### Via Modrinth / CurseForge
1. Télécharger le `.jar` depuis la page du mod
2. Le placer dans `mods/`
3. Lancer Minecraft avec **Fabric Loader ≥ 0.14** et **Fabric API**

### Via GitHub Codespaces (build depuis les sources)
```bash
# 1. Ouvrir ce repo dans Codespaces
# 2. Dans le terminal :
./gradlew build

# Le .jar compilé sera dans :
# build/libs/blockname-1.0.0.jar
```

---

## ⌨️ Raccourcis

| Touche | Action |
|--------|--------|
| `F9`  | Activer / désactiver le Scan |
| `F10` | Activer / désactiver le Debug |

---

## ⚙️ Configuration

Fichier : `config/blockname.json` (créé automatiquement)

```json
{
  "scan": {
    "enabled": true,
    "show_tool_required": true,
    "show_silk_touch": true,
    "show_fortune": true,
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

## 🔧 Changer de version Minecraft

Éditer `gradle.properties` et décommenter la section de version souhaitée, puis :

```bash
./gradlew build
```

---

## 🤝 Dépendances

| Dépendance | Obligatoire |
|-----------|-------------|
| Fabric Loader ≥ 0.14 | ✅ |
| Fabric API | ✅ |
| ModMenu | ⭐ Recommandé |

---

## 📄 Licence

MIT — voir [LICENSE](LICENSE)

*Fait avec ❤️ par Dominique*
