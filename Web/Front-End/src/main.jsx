import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import './index.css';
import './shared/styles/design-system.css';
import './shared/i18n/i18n.js';
import { EnvironmentProvider } from './context/EnvironmentContext';
import GlobalAccessibilityProvider from './shared/components/GlobalAccessibilityProvider/GlobalAccessibilityProvider';
import { ToastProvider } from './shared/components/Toast/Toast';
import {
  applyAccessibilitySettings,
  getAccessibilitySettings,
} from './shared/accessibility/accessibilitySettings';

// Restaurar accesibilidad antes del primer render.
applyAccessibilitySettings(getAccessibilitySettings());

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <ToastProvider>
        <EnvironmentProvider>
          <GlobalAccessibilityProvider>
            <App />
          </GlobalAccessibilityProvider>
        </EnvironmentProvider>
      </ToastProvider>
    </BrowserRouter>
  </React.StrictMode>
);
