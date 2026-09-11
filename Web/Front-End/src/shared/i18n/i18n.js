import i18n from 'i18next'; //cambiamos este
import { initReactI18next } from 'react-i18next';
import en from './locales/en.json';
import es from './locales/es.json';
import fr from './locales/fr.json';
import pt from './locales/pt.json';

const savedLang = localStorage.getItem('language') || 'es';
const validLangs = ['es', 'en', 'fr', 'pt'];
const lng = validLangs.includes(savedLang) ? savedLang : 'es';

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: en },
    es: { translation: es },
    fr: { translation: fr },
    pt: { translation: pt },
  },

  lng,
  fallbackLng: 'es',

  keySeparator: '.',
  nsSeparator: false,

  interpolation: {
    escapeValue: false,
  },

  react: {
    useSuspense: false,
  },
});

export default i18n;