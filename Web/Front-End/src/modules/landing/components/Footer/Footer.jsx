import './Footer.css';
import logo from '../../../../shared/assets/EduAirControlLogo.png';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

import { ShieldCheck, Mail, Phone, MapPin } from 'lucide-react';

import ScrollLink from '../../../../shared/components/ScrollLink/ScrollLink';

function Footer() {
  const { t } = useTranslation();

  return (
    <footer id="footer" className="footer">
      <div className="footer-container">
        {/* Logo */}
        <div className="footer-brand"
          onClick={() => {
            window.scrollTo({
              top: 0,
              behavior: 'smooth',
            });
          }}
        >

          <div className="footer-logo">
            <img src={logo} alt="EduAirControl" />
            <span>EduAirControl</span>
          </div>
          

          <p>{t('landing.footer.description')}</p>
        </div>

        {/* Navegación */}
        <div className="footer-links">
          <h3>{t('landing.footer.navTitle')}</h3>

          <ScrollLink to="hero">{t('landing.navbar.home')}</ScrollLink>

          <ScrollLink to="why">{t('landing.navbar.why')}</ScrollLink>

          <ScrollLink to="how">{t('landing.navbar.howItWorks')}</ScrollLink>

          <ScrollLink to="designed">{t('landing.navbar.designedFor')}</ScrollLink>

          <ScrollLink to="cta">{t('landing.navbar.cta')}</ScrollLink>
        </div>

        {/* Recursos */}
        <div className="footer-links">
          <h3>{t('landing.footer.resourcesTitle')}</h3>

          <Link to="/login">{t('landing.footer.loginLink')}</Link>
          <Link to="/login">{t('landing.footer.signupLink')}</Link>
          <Link to="/terms">{t('landing.footer.termsLink')}</Link>
        </div>

        {/* Contacto */}
        <div className="footer-contact">
          <h3>{t('landing.footer.contactTitle')}</h3>

          <div>
            <Mail size={18} />
            <span>eduaricontrol@gmail.com</span>
          </div>

          <div>
            <Phone size={18} />
            <span>+57 3229523486</span>
          </div>

          <div>
            <MapPin size={18} />
            <span>Neiva, Huila - Colombia</span>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <p>{t('landing.footer.copyright')}</p>
      </div>
    </footer>
  );
}

export default Footer;
