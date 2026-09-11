import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { HiOutlineDocumentText, HiCheckCircle } from 'react-icons/hi2';
import './Terms.css';
import background from '../../../../shared/assets/fondo-terms.png';

function TermsScreen() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [hasReachedEnd, setHasReachedEnd] = useState(false);
  const localizedTerms = t('terms', { returnObjects: true });
  const nestedTerms = t('signup.terms', { returnObjects: true });
  const terms = Array.isArray(localizedTerms?.sections) ? localizedTerms : nestedTerms;
  const sections = Array.isArray(terms?.sections) ? terms.sections : [];

  useEffect(() => {
    const verifyScrollEnd = () => {
      const { scrollHeight } = document.documentElement;
      setHasReachedEnd(window.scrollY + window.innerHeight >= scrollHeight - 24);
    };

    verifyScrollEnd();
    window.addEventListener('scroll', verifyScrollEnd, { passive: true });
    window.addEventListener('resize', verifyScrollEnd);
    return () => {
      window.removeEventListener('scroll', verifyScrollEnd);
      window.removeEventListener('resize', verifyScrollEnd);
    };
  }, []);

  const acceptTerms = () => {
    sessionStorage.setItem('eduaircontrol-terms-read', 'true');
    navigate('/login');
  };

  return (
    <main className="terms-page" style={{ backgroundImage: `url(${background})` }}>
      <article className="terms-container">
        <header className="terms-header">
          <div className="terms-icon">
            <HiOutlineDocumentText />
          </div>
          <h1>{terms.title || t('terms.title')}</h1>
          <p className="terms-brand">{terms.brand || t('terms.brand')}</p>
          <p className="terms-subtitle">{terms.subtitle || t('terms.subtitle')}</p>
        </header>

        <div className="terms-content">
          {sections.map((section) => (
            <section className="legal-section" key={section.title}>
              <h2>{section.title}</h2>
              {section.paragraphs?.map((paragraph) => (
                <p key={paragraph}>{paragraph}</p>
              ))}
              {section.list && (
                <ul className="legal-list">
                  {section.list.map((item) => (
                    <li key={item}>
                      <HiCheckCircle />
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              )}
              {section.note && <p className="legal-note">{section.note}</p>}
            </section>
          ))}
          <footer className="terms-metadata">
            <p>
              <strong>{terms.metadata?.updated || t('terms.metadata.updated')}:</strong>{' '}
              {terms.metadata?.updatedValue || t('terms.metadata.updatedValue')}
            </p>
            <p>
              <strong>{terms.metadata?.responsible || t('terms.metadata.responsible')}:</strong>{' '}
              {terms.metadata?.responsibleValue || t('terms.metadata.responsibleValue')}
            </p>
            <p>
              <strong>{terms.metadata?.email || t('terms.metadata.email')}:</strong>{' '}
              {terms.metadata?.emailValue || t('terms.metadata.emailValue')}
            </p>
          </footer>
        </div>

        {!hasReachedEnd && (
          <p className="terms-read-hint">{terms.readToEnd || t('terms.readToEnd')}</p>
        )}
        <button className="btn-accept" disabled={!hasReachedEnd} onClick={acceptTerms}>
          {terms.acceptBtn || t('terms.acceptBtn')}
        </button>
      </article>
    </main>
  );
}

export default TermsScreen;
