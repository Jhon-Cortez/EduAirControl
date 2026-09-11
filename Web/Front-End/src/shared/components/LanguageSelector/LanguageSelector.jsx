import { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { FaChevronDown } from 'react-icons/fa';
import i18n from '../../i18n/i18n';

const LANGUAGES = [
  {
    code: 'es',
    label: 'Español',
    flag: 'https://flagcdn.com/w40/es.png',
  },
  {
    code: 'en',
    label: 'English',
    flag: 'https://flagcdn.com/w40/us.png',
  },
  {
    code: 'fr',
    label: 'Français',
    flag: 'https://flagcdn.com/w40/fr.png',
  },
  {
    code: 'pt',
    label: 'Português',
    flag: 'https://flagcdn.com/w40/pt.png',
  },
];

function LanguageSelector({ openUp = false }) {
  const { i18n: i18nInstance } = useTranslation();
  const [showLangs, setShowLangs] = useState(false);
  const ref = useRef(null);

  const currentLang = LANGUAGES.find((l) => l.code === i18nInstance.language) || LANGUAGES[0];

  useEffect(() => {
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setShowLangs(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const handleChange = (code) => {
    i18n.changeLanguage(code);
    localStorage.setItem('language', code);
    setShowLangs(false);
  };

  return (
    <div className="language-selector-premium" ref={ref}>
      <button type="button" className="lang-btn-premium" onClick={() => setShowLangs((v) => !v)}>
        <img src={currentLang.flag} alt={currentLang.label} className="lang-flag-img" />
        <span className="lang-label-text">{currentLang.label}</span>
        <FaChevronDown className={`lang-arrow ${showLangs ? 'open' : ''}`} />
      </button>

      {showLangs && (
        <div className={`lang-dropdown-premium ${openUp ? 'lang-dropdown-premium--up' : ''}`}>
          {LANGUAGES.map((lang) => (
            <button
              key={lang.code}
              type="button"
              className={`lang-option-premium ${lang.code === i18nInstance.language ? 'active' : ''}`}
              onClick={() => handleChange(lang.code)}
            >
              <img src={lang.flag} alt={lang.label} className="lang-flag-img-dropdown" />
              <span>{lang.label}</span>
              {lang.code === i18nInstance.language && <span className="lang-check">✓</span>}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default LanguageSelector;
