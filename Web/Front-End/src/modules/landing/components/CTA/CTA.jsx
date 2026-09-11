import { Link } from 'react-router-dom';
import { ArrowRight, LogIn } from 'lucide-react';
import './CTA.css';
import { useTranslation } from 'react-i18next';

function CTA() {
  const { t } = useTranslation();

  return (
    <section id="cta" className="cta">
      <div className="cta-container">
        <span className="cta-badge">{t('landing.cta.badge')}</span>

        <h2>
          {t('landing.cta.title')}
          <span> {t('landing.cta.titleHighlight')}</span>
        </h2>

        <p>{t('landing.cta.description')}</p>

        <div className="cta-buttons">
          <Link to="/login?panel=register" className="btn-primary">
            <LogIn size={20} />
            {t('landing.cta.registerBtn')}
          </Link>
        </div>
      </div>
    </section>
  );
}

export default CTA;
