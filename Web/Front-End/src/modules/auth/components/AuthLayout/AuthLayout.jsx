import './AuthLayout.css';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { FaChevronDown } from 'react-icons/fa';

function AuthLayout({ children, className = '' }) {
  const { i18n } = useTranslation();
  const [showLangs, setShowLangs] = useState(false);

  const languages = [
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

  const currentLang = languages.find((lang) => lang.code === i18n.language) || languages[0];

  const changeLanguage = (code) => {
    i18n.changeLanguage(code);
    localStorage.setItem('language', code);
    setShowLangs(false);
  };

  return (
    <div className={`auth-container ${className}`.trim()}>
      {/* Selector de Idiomas Fijo en la Esquina Superior Derecha */}
      <div className="language-selector-fixed">
        <button className="lang-btn-premium" onClick={() => setShowLangs(!showLangs)}>
          <img src={currentLang.flag} alt={currentLang.label} className="lang-flag-img" />
          <span className="lang-label-text">{currentLang.label}</span>
          <FaChevronDown className={`lang-arrow ${showLangs ? 'open' : ''}`} />
        </button>

        {showLangs && (
          <div className="lang-dropdown-premium">
            {languages.map((lang) => (
              <button
                key={lang.code}
                className={`lang-option-premium ${i18n.language === lang.code ? 'active' : ''}`}
                onClick={() => changeLanguage(lang.code)}
              >
                <img src={lang.flag} alt={lang.label} className="lang-flag-img-dropdown" />
                <span>{lang.label}</span>
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="auth-overlay"></div>

      <div className="auth-card">{children}</div>
    </div>
  );
}

export default AuthLayout;
