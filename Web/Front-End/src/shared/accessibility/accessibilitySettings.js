export const ACCESSIBILITY_STORAGE_KEYS = {
  fontSize: 'a11y-font-size',
  darkMode: 'darkMode',
  colorTheme: 'a11y-color-theme',
  legacyTheme: 'theme',
};

export const ACCESSIBILITY_THEMES = [
  '',
  'theme-protanopia',
  'theme-deuteranopia',
  'theme-tritanopia',
];

const CONTROLLED_BODY_CLASSES = [...ACCESSIBILITY_THEMES.filter(Boolean), 'dark-mode'];

const FONT_SIZES = { base: '16px', lg: '18px', xl: '20px' };

function readDarkMode() {
  try {
    return (
      JSON.parse(localStorage.getItem(ACCESSIBILITY_STORAGE_KEYS.darkMode) || 'false') === true
    );
  } catch {
    return false;
  }
}

export function getAccessibilitySettings() {
  const savedTheme = localStorage.getItem(ACCESSIBILITY_STORAGE_KEYS.colorTheme);
  const legacyTheme = localStorage.getItem(ACCESSIBILITY_STORAGE_KEYS.legacyTheme);
  const colorTheme = ACCESSIBILITY_THEMES.includes(savedTheme ?? '')
    ? (savedTheme ?? '')
    : ACCESSIBILITY_THEMES.includes(legacyTheme ?? '')
      ? (legacyTheme ?? '')
      : '';
  const fontSize = localStorage.getItem(ACCESSIBILITY_STORAGE_KEYS.fontSize) || 'base';

  return {
    fontSize: FONT_SIZES[fontSize] ? fontSize : 'base',
    darkMode: readDarkMode(),
    colorTheme,
  };
}

export function applyAccessibilitySettings(settings = getAccessibilitySettings()) {
  const fontSize = FONT_SIZES[settings.fontSize] ? settings.fontSize : 'base';
  const colorTheme = ACCESSIBILITY_THEMES.includes(settings.colorTheme) ? settings.colorTheme : '';

  document.documentElement.style.setProperty('--a11y-font-size', FONT_SIZES[fontSize]);
  document.documentElement.setAttribute('data-font-size', fontSize);
  document.documentElement.setAttribute('data-theme', settings.darkMode ? 'dark' : 'light');

  document.body.classList.remove(...CONTROLLED_BODY_CLASSES);
  if (colorTheme) document.body.classList.add(colorTheme);
  if (settings.darkMode) document.body.classList.add('dark-mode');
}

// Devuelve el tamaño de fuente accesible actual en píxeles (número).
export function getComputedA11yFontSizePx() {
  try {
    const v = getComputedStyle(document.documentElement)
      .getPropertyValue('--a11y-font-size')
      .trim();
    return v.endsWith('px') ? parseInt(v.replace('px', ''), 10) : parseInt(v, 10) || 16;
  } catch {
    return 16;
  }
}

export function saveAccessibilitySettings(settings) {
  localStorage.setItem(ACCESSIBILITY_STORAGE_KEYS.fontSize, settings.fontSize);
  localStorage.setItem(ACCESSIBILITY_STORAGE_KEYS.darkMode, JSON.stringify(settings.darkMode));
  localStorage.setItem(ACCESSIBILITY_STORAGE_KEYS.colorTheme, settings.colorTheme);
  localStorage.setItem(ACCESSIBILITY_STORAGE_KEYS.legacyTheme, settings.colorTheme);
  applyAccessibilitySettings(settings);
  window.dispatchEvent(new CustomEvent('a11y-change'));
}

export function resetAccessibilitySettings() {
  saveAccessibilitySettings({ fontSize: 'base', darkMode: false, colorTheme: '' });
}
