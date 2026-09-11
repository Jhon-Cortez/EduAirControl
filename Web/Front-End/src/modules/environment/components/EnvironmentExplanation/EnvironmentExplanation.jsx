import { useTranslation } from 'react-i18next';
import './EnvironmentExplanation.css';

function EnvironmentExplanation() {
  const { t } = useTranslation();

  return (
    <section className="environment-explanation">
      <h3>{t('allEnvironments.environmentIndex')}</h3>

      <p>{t('allEnvironments.environmentIndexDescription')}</p>

      <div className="environment-levels">
        <div className="level excellent">
          <span>90 - 100</span>
          <small>{t('allEnvironments.excellent')}</small>
        </div>

        <div className="level good">
          <span>70 - 89</span>
          <small>{t('allEnvironments.good')}</small>
        </div>

        <div className="level regular">
          <span>50 - 69</span>
          <small>{t('allEnvironments.regular')}</small>
        </div>

        <div className="level bad">
          <span>0 - 49</span>
          <small>{t('allEnvironments.bad')}</small>
        </div>
      </div>
    </section>
  );
}

export default EnvironmentExplanation;
