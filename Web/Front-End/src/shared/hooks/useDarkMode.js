import { useState, useEffect } from 'react';
import {
  applyAccessibilitySettings,
  getAccessibilitySettings,
  saveAccessibilitySettings,
} from '../accessibility/accessibilitySettings';

/**
 * Aplica las clases al body preservando otras clases existentes.
 */
export function useDarkMode() {
  const [darkMode, setDarkModeState] = useState(() => getAccessibilitySettings().darkMode);

  const setDarkMode = (val) => {
    const settings = getAccessibilitySettings();
    saveAccessibilitySettings({ ...settings, darkMode: val });
    setDarkModeState(val);
  };

  useEffect(() => {
    applyAccessibilitySettings(getAccessibilitySettings());
    const handleSettingsChange = () => setDarkModeState(getAccessibilitySettings().darkMode);
    window.addEventListener('a11y-change', handleSettingsChange);
    return () => window.removeEventListener('a11y-change', handleSettingsChange);
  }, [darkMode]);

  return [darkMode, setDarkMode];
}
