import './HeroContent.css';
import { Link } from 'react-router-dom';
import { ShieldCheck } from 'lucide-react';
import { useTranslation } from 'react-i18next';

function HeroContent() {
  const { t } = useTranslation();

  return (
    <div className="hero-content">
      <div className="hero-badge">
        <ShieldCheck size={16} />

        <span>{t('landing.hero.badge')}</span>
      </div>

      <h1 className="hero-title">
        <span className="hero-title-main">
          {t('landing.hero.title1')} {t('landing.hero.title2')}
        </span>
        <span className="hero-title-brand">{t('landing.hero.title3')}</span>
      </h1>

      <p className="hero-description">{t('landing.hero.description')}</p>

      <div className="hero-actions">
        <Link to="/login?panel=register" className="primary-btn">
          {t('landing.hero.downloadApp')}

          <span className="btn-arrow">→</span>
        </Link>

        <Link to="/guide" className="secondary-btn">
          {t('landing.hero.viewDemo')}
        </Link>
      </div>

      <div className="hero-stats">
        <div className="stat-item">
          <h3>{t('landing.hero.stat1Value')}</h3>
          <span>{t('landing.hero.stat1Label')}</span>
        </div>

        <div className="stat-item">
          <h3>{t('landing.hero.stat2Value')}</h3>
          <span>{t('landing.hero.stat2Label')}</span>
        </div>

        <div className="stat-item">
          <h3>{t('landing.hero.stat3Value')}</h3>
          <span>{t('landing.hero.stat3Label')}</span>
        </div>
      </div>

      <div className="trusted-section">
        <div className="trusted-line"></div>

        <span className="trusted-title">{t('landing.hero.trustedBy')}</span>

        <div className="trusted-items">
          <div className="trusted-item">
            🏫
            <span>{t('landing.hero.trusted1')}</span>
          </div>

          <div className="trusted-item">
            🎓
            <span>{t('landing.hero.trusted2')}</span>
          </div>

          <div className="trusted-item">
            🧪
            <span>{t('landing.hero.trusted3')}</span>
          </div>

          <div className="trusted-item">
            🏢
            <span>{t('landing.hero.trusted4')}</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HeroContent;
