# AGENTS.md — EduAirControl Frontend

Guide for AI agents and developers working on this codebase.

---

## 1. Tech Stack

- React 18 + Vite
- react-router-dom v6
- i18next (4 locales: es, en, fr, pt)
- react-hook-form + Zod (auth forms)
- Recharts (dashboard charts)
- react-icons + lucide-react (icons)
- JSON Server (mock REST DB at `:3001`)
- Spring Boot backend (auth at `:8080`)
- Plain CSS (no modules, no Sass, no Tailwind)

---

## 2. Architecture — Feature-Based Modules

```
src/
├── modules/                  # Domain modules
│   └── [module]/
│       ├── pages/            # Page-level components (screens)
│       ├── components/       # UI components specific to this module
│       ├── services/         # HTTP/data access layer
│       ├── viewmodels/       # Custom hooks with business logic
│       ├── utils/            # Pure helper functions
│       ├── constants/        # Module-specific constants
│       ├── data/             # Mock/seed data (temporary)
│       └── schemas/          # Zod validation schemas
│
├── shared/                   # Cross-module reusable layer
│   ├── components/           # Button, Modal, Toast, Input, etc.
│   ├── services/             # apiClient, dbClient
│   ├── hooks/                # useDarkMode, useDateFormat
│   ├── styles/               # design-system.css, backgrounds.css
│   ├── i18n/                 # i18n config + locale JSONs
│   ├── accessibility/        # Accessibility settings manager
│   └── assets/               # Static images
│
├── context/                  # Global React Context (EnvironmentContext)
├── viewmodels/               # Barrel re-export of all VMs
├── App.jsx                   # Route definitions
├── main.jsx                  # Entry point
├── index.css                 # CSS tokens, dark mode, global reset
└── db.json                   # JSON Server database
```

---

## 3. Naming Conventions

| Type | Convention | Example |
|------|-----------|---------|
| Module folders | `kebab-case` | `dashboard/`, `environment/` |
| Component folders | `PascalCase` | `Button/`, `EnvironmentCard/` |
| Component files | `PascalCase.jsx` | `DashboardScreen.jsx` |
| Page files | `PascalCase + Screen.jsx` | `LoginScreen.jsx`, `AllEnvironmentsScreen.jsx` |
| ViewModel hooks | `use[Name]VM.js` | `useDashboardVM.js`, `useProfileVM.js` |
| Service files | `[name]Service.js` | `authService.js`, `environmentService.js` |
| Schema files | `[name]Schema.jsx` | `loginSchema.jsx`, `registerSchema.jsx` |
| Utility files | `camelCase.js` | `environmentHelpers.js`, `calculateEnvironmentScore.js` |
| Constant files | `camelCase.js` | `environments.js`, `metricDefinitions.jsx` |
| CSS files | `PascalCase.css` (co-located) | `DashboardScreen.css` |
| Hook files | `use[Name].js` | `useDarkMode.js`, `useNotifications.js` |

---

## 4. Import Rules

Always use relative paths. No path aliases.

```js
// From a component inside a module
import { useEnvironments } from '../../../context/EnvironmentContext';
import { Modal, Button } from '../../../shared/components';
import apiClient from '../../../shared/services/apiClient';

// Cross-module (avoid when possible, prefer context)
import { calcScore } from '../../environment/utils/environmentHelpers';

// Barrel imports (when available)
import { Button, Modal } from '../../../shared/components';
```

Import order:
1. External libraries (react, react-router, i18next)
2. Internal modules (context, shared, other modules)
3. Local files (./Component, ./hooks, ./utils)
4. CSS files (last)

---

## 5. State Management

### Layer 1: React Context (Global)
- `EnvironmentContext` — environments CRUD, loading state
- `ToastContext` — ephemeral notifications

### Layer 2: ViewModel Hooks (Per Screen)
Each complex screen has a `use[Name]VM.js` that:
- Consumes context via `useEnvironments()`
- Adds local state with `useState`/`useMemo`
- Returns both state and action functions
- Lives in `modules/[module]/viewmodels/`

### Layer 3: Component-Local State
- `useState` for UI concerns (dropdowns, toggles, modals)
- `react-hook-form` + Zod for forms (auth module)

### Persistence
- `localStorage` for: token, user, darkMode, language, timezone, dateFormat, reminders

---

## 6. Service Layer

Two HTTP clients in `src/shared/services/apiClient.js`:

| Client | Base URL | Purpose | Used by |
|--------|----------|---------|---------|
| `apiClient` | `http://localhost:8080` | Spring Boot backend (auth) | authService |
| `dbClient` | `http://localhost:3001` | JSON Server (data CRUD) | environmentService, sensorService, profileService |

Rules:
- Services handle all HTTP calls. Never use `fetch()` directly in components.
- Services return parsed JSON. Errors are thrown as `Error` objects.
- When backend endpoints are ready, change `dbClient` calls to `apiClient`.

---

## 7. CSS & Design System

### Strategy
- Plain CSS files co-located with components (no CSS Modules)
- Global tokens in `src/index.css` (CSS custom properties)
- Reusable classes in `src/shared/styles/design-system.css`

### Design System Classes
- `ds-card`, `ds-card--stat`, `ds-card--hoverable` — cards
- `ds-btn`, `ds-btn--primary`, `ds-btn--outline` — buttons
- `ds-modal`, `ds-overlay`, `ds-panel` — modals and overlays
- `ds-field`, `ds-field__input` — form fields
- `ds-toast`, `ds-spinner`, `ds-empty-state` — utilities

### Dark Mode
- Toggle via `body.dark-mode` class
- CSS variables switch automatically: `[data-theme="dark"]`
- Color themes: `body.theme-protanopia`, `body.theme-deuteranopia`, `body.theme-tritanopia`

### Focus Styles
- Always add `:focus-visible` when using `outline: none`
- Pattern: `outline: 2px solid var(--accent); outline-offset: 2px;`

---

## 8. Internationalization (i18n)

- Library: `i18next` + `react-i18next`
- 4 locales: `es` (default), `en`, `fr`, `pt`
- Flat dot-separated keys: `login.email`, `landing.navbar.home`
- All visible text must use `t('key')`
- Language stored in `localStorage.getItem('language')`

```js
const { t } = useTranslation();
<span>{t('login.email')}</span>
```

---

## 9. Git Workflow

1. Create feature branch from `develop`
2. Make changes
3. Commit with concise title (no description body)
4. Merge back to `develop` with `--no-ff`
5. Push both branches

```
git checkout -b feat/my-feature
# work...
git add -A && git commit -m "feat(module): short description"
git checkout develop && git merge feat/my-feature --no-ff -m "Merge branch 'feat/my-feature' into develop"
git push origin develop && git push origin feat/my-feature
```

---

## 10. Rules for New Code

1. **Branch always** — never commit directly to `develop`
2. **Components in PascalCase folders** — `MyComponent/MyComponent.jsx`
3. **Use design system first** — prefer `ds-*` classes over custom CSS
4. **i18n everything** — all visible text goes through `t()`
5. **Services for HTTP** — never `fetch()` in components
6. **One VM per complex screen** — encapsulate logic in `use*VM`
7. **No hardcoded colors** — use CSS custom properties (`var(--accent)`)
8. **Accessible** — `aria-label` on icon buttons, `role="dialog"` on modals, focus management
9. **Co-locate CSS** — component CSS lives next to the component
10. **Clean imports** — relative paths, ordered (external → shared → local → CSS)

---

## 11. Mobile Adaptation Guide (React Native / Expo)

### What Reuses Directly (No Changes)
- `src/modules/*/viewmodels/` — all ViewModel hooks
- `src/modules/*/services/` — all service files
- `src/modules/*/utils/` — all utility functions
- `src/modules/*/constants/` — all constants
- `src/modules/*/schemas/` — Zod schemas (if using zod in RN)
- `src/shared/i18n/` — all locale JSON files
- `src/shared/services/apiClient.js` — HTTP clients (change `fetch` to RN fetch if needed)
- `src/shared/accessibility/` — settings manager
- `src/shared/hooks/` — custom hooks (logic only)
- `src/context/` — React Context providers (work in RN)

### What Does NOT Reuse
- All CSS files — replaced by `StyleSheet.create()` or NativeWind
- HTML elements (`<div>`, `<span>`, `<input>`) — replaced by RN components (`<View>`, `<Text>`, `<TextInput>`)
- `react-icons` / `lucide-react` — replaced by `react-native-vector-icons` or `@expo/vector-icons`
- `react-router-dom` — replaced by `@react-navigation/native`
- `recharts` — replaced by `react-native-chart-kit` or `victory-native`
- `react-hook-form` — works in RN but input components differ
- `localStorage` — replaced by `AsyncStorage`
- `window`, `document` APIs — not available in RN

### Mobile Project Structure
```
mobile/
├── src/
│   ├── modules/              # SAME viewmodels, services, utils, constants
│   │   └── [module]/
│   │       ├── screens/      # (was pages/) — RN screen components
│   │       ├── components/   # RN-specific UI components
│   │       ├── viewmodels/   # REUSE from web (copy or symlink)
│   │       ├── services/     # REUSE from web
│   │       ├── utils/        # REUSE from web
│   │       └── constants/    # REUSE from web
│   ├── shared/
│   │   ├── components/       # RN versions of Button, Modal, Toast, etc.
│   │   ├── hooks/            # REUSE from web
│   │   ├── i18n/             # REUSE locale JSONs from web
│   │   ├── services/         # REUSE apiClient from web
│   │   └── theme/            # RN StyleSheet tokens (replaces CSS variables)
│   ├── navigation/           # React Navigation setup (replaces react-router)
│   ├── context/              # REUSE providers from web
│   └── App.tsx
```

### Component Mapping (Web → Native)

| Web | React Native |
|-----|-------------|
| `<div>` | `<View>` |
| `<span>`, `<p>`, `<h1>` | `<Text>` |
| `<input>`, `<textarea>` | `<TextInput>` |
| `<button>` | `<TouchableOpacity>` / `<Pressable>` |
| `<img>` | `<Image>` |
| `<a>` | `<Link>` (Expo) or navigation `onPress` |
| `<select>` | `<Picker>` or custom modal selector |
| CSS `flexbox` | `flexDirection` (default column in RN) |
| CSS `margin/padding` | `margin*`/`padding*` (shorthand not supported) |
| `onClick` | `onPress` |
| `className` | `style` |
| `:hover`, `:focus` | `onPressIn`/`onPressOut` or `useState` |
| `outline: none` | Not needed (no outline in RN) |

### Sharing Strategy

**Option A: Monorepo (recommended)**
```
EduAirControl/
├── packages/
│   ├── core/           # Shared: viewmodels, services, utils, constants, i18n
│   ├── web/            # React web app (imports from core)
│   └── mobile/         # React Native app (imports from core)
```

**Option B: Copy + Adapt**
- Copy `viewmodels/`, `services/`, `utils/`, `constants/` to mobile
- Adapt only what differs (AsyncStorage vs localStorage, RN fetch)

### AsyncStorage vs localStorage

```js
// Web (localStorage)
localStorage.setItem('token', token);
localStorage.getItem('token');

// Mobile (AsyncStorage)
import AsyncStorage from '@react-native-async-storage/async-storage';
await AsyncStorage.setItem('token', token);
await AsyncStorage.getItem('token');
```

Create a `storage.js` adapter to abstract this difference:

```js
// shared/services/storage.js
import { Platform } from 'react-native';

const storage = {
  async getItem(key) {
    if (Platform.OS === 'web') return localStorage.getItem(key);
    const AsyncStorage = (await import('@react-native-async-storage/async-storage')).default;
    return AsyncStorage.getItem(key);
  },
  async setItem(key, value) {
    if (Platform.OS === 'web') { localStorage.setItem(key, value); return; }
    const AsyncStorage = (await import('@react-native-async-storage/async-storage')).default;
    await AsyncStorage.setItem(key, value);
  },
  async removeItem(key) {
    if (Platform.OS === 'web') { localStorage.removeItem(key); return; }
    const AsyncStorage = (await import('@react-native-async-storage/async-storage')).default;
    await AsyncStorage.removeItem(key);
  },
};

export default storage;
```

---

## 12. Scripts

```bash
npm run dev:fe      # Vite dev server only
npm run server       # JSON Server only
npm run dev          # Both (concurrent)
npm run build        # Production build
npm run lint         # ESLint
```
