import './WhyChoose.css';
import { Cpu, BellRing, BarChart3, ShieldCheck } from 'lucide-react';
import { useTranslation } from 'react-i18next';

function WhyChoose() {
  const { t } = useTranslation();

  return (
    <section id="why" className="why">
      <span className="why-badge">{t('landing.why.badge')}</span>

      <h2>
        {t('landing.why.title')}
        <span> {t('landing.why.titleHighlight')}</span>
      </h2>

      <p className="why-description">{t('landing.why.description')}</p>

      <div className="section-divider"></div>

      <div className="why-grid">
        <div className="why-card">
          <Cpu size={34} />
          <h3>{t('landing.why.card1Title')}</h3>
          <p>{t('landing.why.card1Desc')}</p>
        </div>

        <div className="why-card">
          <BellRing size={34} />
          <h3>{t('landing.why.card2Title')}</h3>
          <p>{t('landing.why.card2Desc')}</p>
        </div>

        <div className="why-card">
          <BarChart3 size={34} />
          <h3>{t('landing.why.card3Title')}</h3>
          <p>{t('landing.why.card3Desc')}</p>
        </div>

        <div className="why-card">
          <ShieldCheck size={34} />
          <h3>{t('landing.why.card4Title')}</h3>
          <p>{t('landing.why.card4Desc')}</p>
        </div>
      </div>
    </section>
  );
}

export default WhyChoose;
