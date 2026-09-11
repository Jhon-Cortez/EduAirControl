import './Navbar.css';
import logo from '../../../../shared/assets/EduAirControlLogo.png';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Menu, X } from 'lucide-react';
import ScrollLink from '../../../../shared/components/ScrollLink/ScrollLink';
import { useTranslation } from 'react-i18next';
import LanguageSelector from '../../../../shared/components/LanguageSelector/LanguageSelector';

function Navbar() {
  const { t } = useTranslation();
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 40);
    };

    window.addEventListener('scroll', handleScroll);

    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    const closeOnDesktop = () => {
      if (window.innerWidth > 1120) setMenuOpen(false);
    };

    window.addEventListener('resize', closeOnDesktop);

    return () => window.removeEventListener('resize', closeOnDesktop);
  }, []);

  useEffect(() => {
    if (!menuOpen) return undefined;

    const closeOnEscape = (event) => {
      if (event.key === 'Escape') setMenuOpen(false);
    };

    window.addEventListener('keydown', closeOnEscape);

    return () => window.removeEventListener('keydown', closeOnEscape);
  }, [menuOpen]);

  const closeMenu = () => setMenuOpen(false);

  return (
    <>
      <header className={`landing-navbar ${scrolled ? 'landing-navbar--scrolled' : ''}`}>
        {/* Logo */}

        <div
          className="landing-navbar__brand"
          onClick={() => {
            window.scrollTo({
              top: 0,
              behavior: 'smooth',
            });
            closeMenu();
          }}
        >
          <img src={logo} alt="EduAirControl" />

          <span>EduAirControl</span>
        </div>

        {/* Navegación Desktop */}

        <div className="landing-navbar__links">
          <ScrollLink to="hero">{t('landing.navbar.home')}</ScrollLink>

          <ScrollLink to="why">{t('landing.navbar.why')}</ScrollLink>

          <ScrollLink to="how">{t('landing.navbar.howItWorks')}</ScrollLink>

          <ScrollLink to="designed">{t('landing.navbar.designedFor')}</ScrollLink>

          <ScrollLink to="cta">{t('landing.navbar.cta')}</ScrollLink>
        </div>

        {/* Acciones Desktop */}

        <div className="landing-navbar__actions">
          <LanguageSelector />

          <button
            type="button"
            className="landing-navbar__login"
            onClick={() => navigate('/login')}
          >
            {t('landing.navbar.login')}
          </button>
        </div>

        {/* Hamburguesa */}

        <button
          type="button"
          className="landing-navbar__toggle"
          onClick={() => setMenuOpen(!menuOpen)}
          aria-label={menuOpen ? 'Cerrar menú de navegación' : 'Abrir menú de navegación'}
          aria-expanded={menuOpen}
          aria-controls="landing-navigation"
        >
          {menuOpen ? <X size={28} /> : <Menu size={28} />}
        </button>

        {/* Menú Mobile */}
      </header>

      <button
        type="button"
        className={`landing-navbar__backdrop ${menuOpen ? 'landing-navbar__backdrop--active' : ''}`}
        onClick={closeMenu}
        aria-hidden="true"
        tabIndex={-1}
      />

      <nav
        id="landing-navigation"
        aria-label="Navegación principal"
        aria-hidden={!menuOpen}
        className={`landing-navbar__menu ${menuOpen ? 'landing-navbar__menu--active' : ''}`}
      >
        <div className="landing-navbar__menu-header">
          <div className="landing-navbar__menu-brand">
            <img src={logo} alt="" />
            <span>EduAirControl</span>
          </div>

          <button
            type="button"
            className="landing-navbar__menu-close"
            onClick={closeMenu}
            aria-label="Cerrar menú de navegación"
          >
            <X size={22} />
          </button>
        </div>

        <div className="landing-navbar__menu-links">
          <ScrollLink to="hero" onClick={closeMenu}>
            {t('landing.navbar.home')}
          </ScrollLink>

          <ScrollLink to="why" onClick={closeMenu}>
            {t('landing.navbar.why')}
          </ScrollLink>

          <ScrollLink to="how" onClick={closeMenu}>
            {t('landing.navbar.howItWorks')}
          </ScrollLink>

          <ScrollLink to="designed" onClick={closeMenu}>
            {t('landing.navbar.designedFor')}
          </ScrollLink>

          <ScrollLink to="cta" onClick={closeMenu}>
            {t('landing.navbar.cta')}
          </ScrollLink>
        </div>

        <div className="landing-navbar__menu-footer">
          <div className="landing-navbar__language">
            <LanguageSelector openUp />
          </div>

          <button
            type="button"
            className="landing-navbar__mobile-login"
            onClick={() => {
              closeMenu();

              navigate('/login');
            }}
          >
            {t('landing.navbar.login')}
          </button>
        </div>
      </nav>
    </>
  );
}

export default Navbar;
