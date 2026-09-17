import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  FaGlobe,
  FaCalendar,
  FaClock,
  FaMoon,
  FaPalette,
  FaMapMarkerAlt,
  FaBell,
  FaShieldAlt,
  FaQuestionCircle,
  FaChevronRight,
  FaLock,
  FaTrash,
  FaEye,
  FaEyeSlash,
  FaInfoCircle,
} from 'react-icons/fa';
import { IoSettings } from 'react-icons/io5';
import { MdEdit } from 'react-icons/md';
import Navbar from '../../dashboard/components/Navbar/Navbar';

// Shared
import { EditModal, Modal, Button } from '../../../shared/components';
import { useDarkMode } from '../../../shared/hooks/useDarkMode';
import { saveDateFormat } from '../../../shared/hooks/useDateFormat';
import {
  ACCESSIBILITY_THEMES,
  getAccessibilitySettings,
  saveAccessibilitySettings,
} from '../../../shared/accessibility/accessibilitySettings';

// CSS
import './Settings.css';

const TIMEZONES = [
  { value: 'America/Bogota', label: 'Bogotá (UTC-5)' },
  { value: 'America/Lima', label: 'Lima (UTC-5)' },
  { value: 'America/Mexico_City', label: 'Ciudad de México (UTC-6)' },
  { value: 'America/New_York', label: 'Nueva York (UTC-5/-4)' },
  { value: 'America/Los_Angeles', label: 'Los Ángeles (UTC-8/-7)' },
  { value: 'America/Sao_Paulo', label: 'São Paulo (UTC-3)' },
  { value: 'America/Santiago', label: 'Santiago (UTC-4/-3)' },
  { value: 'Europe/London', label: 'Londres (UTC+0/+1)' },
  { value: 'Europe/Madrid', label: 'Madrid (UTC+1/+2)' },
  { value: 'Europe/Paris', label: 'París (UTC+1/+2)' },
  { value: 'Asia/Tokyo', label: 'Tokio (UTC+9)' },
  { value: 'Asia/Dubai', label: 'Dubái (UTC+4)' },
  { value: 'Australia/Sydney', label: 'Sídney (UTC+10/+11)' },
];

const THEME_COLORS = {
  '': { light: '#28F4D6', dark: '#28F4D6' },
  'theme-protanopia': { light: '#0072b2', dark: '#4aa8d8' },
  'theme-deuteranopia': { light: '#8a5f00', dark: '#c8880a' },
  'theme-tritanopia': { light: '#a34600', dark: '#e8743a' },
};

const THEMES = ACCESSIBILITY_THEMES.map((key) => ({ key, dot: THEME_COLORS[key] }));

function SettingsScreen() {
  const { t, i18n } = useTranslation();
  const [darkMode, setDarkMode] = useDarkMode();

  const [autoTimezone, setAutoTimezone] = useState(
    () => JSON.parse(localStorage.getItem('autoTimezone')) ?? true
  );
  const [manualTimezone, setManualTimezone] = useState(
    () => localStorage.getItem('manualTimezone') || Intl.DateTimeFormat().resolvedOptions().timeZone
  );
  const [reminders, setReminders] = useState(
    () =>
      JSON.parse(localStorage.getItem('reminders')) || {
        alertas: true,
        advertencias: true,
        resumenDiario: false,
        sonido: true,
      }
  );
  const LANG_NAMES = { es: 'Español', en: 'English', fr: 'Français', pt: 'Português' };
  const LANGUAGES = [
    {
      code: 'es',
      label: 'Español',
      flag: 'https://flagcdn.com/w40/co.png',
      flagAlt: 'Bandera de Colombia',
    },
    { code: 'en', label: 'English', flag: 'https://flagcdn.com/w40/us.png', flagAlt: 'US flag' },
    {
      code: 'fr',
      label: 'Français',
      flag: 'https://flagcdn.com/w40/fr.png',
      flagAlt: 'Drapeau français',
    },
    {
      code: 'pt',
      label: 'Português',
      flag: 'https://flagcdn.com/w40/br.png',
      flagAlt: 'Bandeira do Brasil',
    },
  ];
  const [settings, setSettings] = useState(() => {
    const saved = JSON.parse(localStorage.getItem('settings')) || {
      language: 'English',
      dateFormat: 'DD-MM-YYYY',
    };
    const activeLang = localStorage.getItem('language') || i18n.language || 'es';
    return { ...saved, language: LANG_NAMES[activeLang] || saved.language };
  });
  const [theme, setTheme] = useState(() => getAccessibilitySettings().colorTheme);
  const [modalOpen, setModalOpen] = useState(false);
  const [editField, setEditField] = useState('');
  const [editValue, setEditValue] = useState('');
  const [showLangModal, setShowLangModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [helpModal, setHelpModal] = useState({ open: false, type: null });
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [showPassword, setShowPassword] = useState({ new: false, confirm: false });
  const [passwordData, setPasswordData] = useState({ current: '', new: '', confirm: '' });
  const [passwordError, setPasswordError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState(false);
  const [deleteStep, setDeleteStep] = useState('confirm');

  useEffect(() => {
    localStorage.setItem('autoTimezone', JSON.stringify(autoTimezone));
  }, [autoTimezone]);
  useEffect(() => {
    localStorage.setItem('settings', JSON.stringify(settings));
  }, [settings]);
  useEffect(() => {
    localStorage.setItem('reminders', JSON.stringify(reminders));
  }, [reminders]);
  useEffect(() => {
    const handleAccessibilityChange = () => setTheme(getAccessibilitySettings().colorTheme);
    window.addEventListener('a11y-change', handleAccessibilityChange);
    return () => window.removeEventListener('a11y-change', handleAccessibilityChange);
  }, []);

  const handleTimezoneToggle = (val) => {
    setAutoTimezone(val);
    const tz = val ? Intl.DateTimeFormat().resolvedOptions().timeZone : manualTimezone;
    window.dispatchEvent(new CustomEvent('timezoneChanged', { detail: tz }));
  };
  const handleManualTimezone = (tz) => {
    setManualTimezone(tz);
    localStorage.setItem('manualTimezone', tz);
    window.dispatchEvent(new CustomEvent('timezoneChanged', { detail: tz }));
  };
  const changeTheme = (newTheme) => {
    saveAccessibilitySettings({ ...getAccessibilitySettings(), colorTheme: newTheme, darkMode });
    setTheme(newTheme);
  };
  const handleEdit = (field, value) => {
    setEditField(field);
    setEditValue(value);
    setModalOpen(true);
  };
  const handleSave = (field, newValue) => {
    setSettings((prev) => ({ ...prev, [field]: newValue }));
    if (field === 'dateFormat') saveDateFormat(newValue);
  };
  const handleChangeLanguage = (lang) => {
    i18n.changeLanguage(lang);
    localStorage.setItem('language', lang);
    setSettings((prev) => ({ ...prev, language: LANG_NAMES[lang] || 'English' }));
    setShowLangModal(false);
  };

  const handlePasswordChange = (field, value) => {
    setPasswordError('');
    setPasswordData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSavePassword = () => {
    if (!passwordData.current || !passwordData.new || !passwordData.confirm) {
      setPasswordError(t('settings.passwordModal.errorEmpty'));
      return;
    }
    if (passwordData.new !== passwordData.confirm) {
      setPasswordError(t('settings.passwordModal.errorMatch'));
      return;
    }
    if (passwordData.new.length < 6) {
      setPasswordError(t('settings.passwordModal.errorLength'));
      return;
    }
    // Aquí luego conectas backend
    setPasswordError('');
    setPasswordSuccess(true);
    setTimeout(() => {
      setShowPasswordModal(false);
      setPasswordSuccess(false);
      setPasswordData({ current: '', new: '', confirm: '' });
    }, 1800);
  };

  const handleClosePasswordModal = () => {
    setShowPasswordModal(false);
    setPasswordError('');
    setPasswordSuccess(false);
    setPasswordData({ current: '', new: '', confirm: '' });
  };

  const handleOpenDeleteModal = () => {
    setDeleteStep('confirm');
    setShowDeleteModal(true);
  };

  const handleDeleteRequest = () => {
    // Aquí luego conectas backend / envías email
    setDeleteStep('sent');
  };

  const themeLabel = (key) => {
    const labels = {
      '': t('settings.themeNormal'),
      'theme-protanopia': t('settings.themeProtanopia'),
      'theme-deuteranopia': t('settings.themeDeuteranopia'),
      'theme-tritanopia': t('settings.themeTritanopia'),
    };
    return labels[key] || labels[''];
  };
  const toggleReminder = (key) => setReminders((prev) => ({ ...prev, [key]: !prev[key] }));

  return (
    <div className={`settings-page ${darkMode ? 'dark' : ''}`}>
      <Navbar />
      <main id="main-content" className="settings-content">
        <h1>
          <IoSettings /> {t('settings.title')}
        </h1>

        {/* APARIENCIA */}
        <div className="ds-card--section">
          <h2>
            <FaPalette /> {t('settings.appearance')}
          </h2>
          <div className="settings-field">
            <div className="field-icon">
              <FaMoon />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.darkMode')}</span>
              <span className="field-value">
                {darkMode ? t('settings.enabled') : t('settings.disabled')}
              </span>
            </div>
            <div
              className={`toggle ${darkMode ? 'active' : ''}`}
              onClick={() => setDarkMode(!darkMode)}
            >
              <div className="toggle-circle" />
            </div>
          </div>
          <div className="settings-field theme-field">
            <div className="field-icon">
              <FaPalette />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.accessibleThemes')}</span>
              <span className="field-value">{themeLabel(theme)}</span>
            </div>
          </div>
          <div className="theme-picker">
            {THEMES.map(({ key, dot }) => (
              <button
                key={key}
                className={`theme-card ${theme === key ? 'theme-card--active' : ''}`}
                type="button"
                onClick={() => changeTheme(key)}
                aria-label={`${t('settings.accessibleThemes')}: ${themeLabel(key)}`}
                aria-pressed={theme === key}
              >
                <div className="theme-preview">
                  <div
                    className="preview-bar"
                    style={{ background: darkMode ? dot.dark : dot.light }}
                  />
                  <div className="preview-text">
                    <span className="preview-line" />
                    <span className="preview-line short" />
                  </div>
                </div>
                <span className="theme-label">{themeLabel(key)}</span>
                {theme === key && (
                  <span className="theme-check" aria-hidden="true">
                    ✓
                  </span>
                )}
              </button>
            ))}
          </div>
        </div>

        {/* IDIOMA Y FECHAS */}
        <div className="ds-card--section">
          <h2>
            <FaGlobe /> {t('settings.langAndDates')}
          </h2>
          <p className="section-description">{t('settings.langDescription')}</p>
          <div className="settings-field">
            <div className="field-icon">
              <FaGlobe />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.language')}</span>
              <span className="field-value field-value--lang">
                <img
                  src={LANGUAGES.find((l) => l.label === settings.language)?.flag}
                  alt=""
                  className="field-lang-flag"
                />
                {settings.language}
              </span>
            </div>
            <button className="btn-update" onClick={() => setShowLangModal(true)}>
              <MdEdit /> {t('settings.updateBtn')}
            </button>
          </div>
          <div className="settings-field">
            <div className="field-icon">
              <FaCalendar />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.dateFormat')}</span>
              <span className="field-value">{settings.dateFormat}</span>
            </div>
            <button
              className="btn-update"
              onClick={() => handleEdit('dateFormat', settings.dateFormat)}
            >
              <MdEdit /> {t('settings.updateBtn')}
            </button>
          </div>
          <div className="settings-field">
            <div className="field-icon">
              <FaClock />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.autoTimezone')}</span>
              <span className="field-value">
                {autoTimezone ? t('settings.enabled') : t('settings.disabled')}
              </span>
            </div>
            <div
              className={`toggle ${autoTimezone ? 'active' : ''}`}
              onClick={() => handleTimezoneToggle(!autoTimezone)}
            >
              <div className="toggle-circle" />
            </div>
          </div>
          {!autoTimezone && (
            <div className="settings-field">
              <div className="field-icon">
                <FaMapMarkerAlt />
              </div>
              <div className="field-info">
                <span className="field-label">{t('settings.selectTimezone')}</span>
                <select
                  className="timezone-select"
                  value={manualTimezone}
                  onChange={(e) => handleManualTimezone(e.target.value)}
                >
                  {TIMEZONES.map(({ value, label }) => (
                    <option key={value} value={value}>
                      {label}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          )}
        </div>

        {/* RECORDATORIOS */}
        <div className="ds-card--section">
          <h2>
            <FaBell /> {t('settings.reminders')}
          </h2>
          <p className="section-description">{t('settings.remindersDescription')}</p>
          {[
            { key: 'alertas', label: t('settings.reminderAlerts') },
            { key: 'advertencias', label: t('settings.reminderWarnings') },
            { key: 'resumenDiario', label: t('settings.reminderDaily') },
            { key: 'sonido', label: t('settings.reminderSound') },
          ].map(({ key, label }) => (
            <div key={key} className="settings-field">
              <div className="field-icon">
                <FaBell />
              </div>
              <div className="field-info">
                <span className="field-label">{label}</span>
                <span className="field-value">
                  {reminders[key] ? t('settings.enabled') : t('settings.disabled')}
                </span>
              </div>
              <div
                className={`toggle ${reminders[key] ? 'active' : ''}`}
                onClick={() => toggleReminder(key)}
              >
                <div className="toggle-circle" />
              </div>
            </div>
          ))}
        </div>

        {/* PRIVACIDAD */}
        <div className="ds-card--section">
          <h2>
            <FaShieldAlt /> {t('settings.privacy')}
          </h2>
          <p className="section-description">{t('settings.privacyDescription')}</p>

          <div className="settings-field privacy-info-field">
            <div className="field-icon">
              <FaInfoCircle />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.privacyInfoLabel')}</span>
              <span className="field-value">{t('settings.privacyInfo')}</span>
            </div>
          </div>

          <div className="settings-field">
            <div className="field-icon">
              <FaLock />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.changePassword')}</span>
              <span className="field-value">{t('settings.changePasswordSub')}</span>
            </div>
            <button className="btn-update" onClick={() => setShowPasswordModal(true)}>
              <MdEdit /> {t('settings.updateBtn')}
            </button>
          </div>

          <div
            className="settings-field settings-field--link"
            onClick={() => setHelpModal({ open: true, type: 'privacy' })}
          >
            <div className="field-icon">
              <FaShieldAlt />
            </div>
            <div className="field-info">
              <span className="field-label">{t('settings.viewPrivacyPolicy')}</span>
              <span className="field-value">{t('settings.viewPrivacyPolicySub')}</span>
            </div>
            <FaChevronRight className="settings-chevron" />
          </div>

          <div className="settings-field">
            <div className="field-info">
              <span className="field-label" style={{ color: '#ff4d5b' }}>
                {t('settings.deleteAccount')}
              </span>
              <span className="field-value">{t('settings.deleteAccountSub')}</span>
            </div>
            <button className="btn-delete-icon" onClick={handleOpenDeleteModal} aria-label="Eliminar cuenta">
              <FaTrash />
            </button>
          </div>
        </div>

        {/* AYUDA */}
        <div className="ds-card--section">
          <h2>
            <FaQuestionCircle /> {t('settings.help')}
          </h2>
          <p className="section-description">{t('settings.helpDescription')}</p>
          {[
            { type: 'faq', label: t('settings.helpFaq'), sub: t('settings.helpFaqSub') },
            {
              type: 'contact',
              label: t('settings.helpContact'),
              sub: t('settings.helpContactSub'),
            },
            { type: 'terms', label: t('settings.helpTerms'), sub: t('settings.helpTermsSub') },
            { type: 'version', label: t('settings.helpVersion'), sub: 'v1.0.0' },
          ].map(({ type, label, sub }) => (
            <div
              key={type}
              className="settings-field settings-field--link"
              onClick={() => setHelpModal({ open: true, type })}
            >
              <div className="field-icon">
                <FaQuestionCircle />
              </div>
              <div className="field-info">
                <span className="field-label">{label}</span>
                <span className="field-value">{sub}</span>
              </div>
              <FaChevronRight className="settings-chevron" />
            </div>
          ))}
        </div>
      </main>

      {/* ── MODAL CAMBIAR CONTRASEÑA ── */}
      <Modal isOpen={showPasswordModal} onClose={handleClosePasswordModal} size="sm">
        <h3 className="password-title">🔐 {t('settings.passwordModal.title')}</h3>
        {passwordSuccess ? (
          <div className="password-success">
            <span className="password-success-icon">✓</span>
            <p>{t('settings.passwordModal.success')}</p>
          </div>
        ) : (
          <div className="password-form">
            <input
              type="password"
              aria-label="Contraseña actual"
              placeholder={t('settings.passwordModal.current')}
              value={passwordData.current}
              onChange={(e) => handlePasswordChange('current', e.target.value)}
            />
            <div className="input-password">
              <input
                type={showPassword.new ? 'text' : 'password'}
                aria-label="Nueva contraseña"
                placeholder={t('settings.passwordModal.new')}
                value={passwordData.new}
                onChange={(e) => handlePasswordChange('new', e.target.value)}
              />
              <button
                type="button"
                className="toggle-password"
                onClick={() => setShowPassword((prev) => ({ ...prev, new: !prev.new }))}
                aria-label={showPassword.new ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                aria-pressed={showPassword.new}
              >
                {showPassword.new ? <FaEye /> : <FaEyeSlash />}
              </button>
            </div>
            <div className="input-password">
              <input
                type={showPassword.confirm ? 'text' : 'password'}
                aria-label="Confirmar contraseña"
                placeholder={t('settings.passwordModal.confirm')}
                value={passwordData.confirm}
                onChange={(e) => handlePasswordChange('confirm', e.target.value)}
              />
              <button
                type="button"
                className="toggle-password"
                onClick={() => setShowPassword((prev) => ({ ...prev, confirm: !prev.confirm }))}
                aria-label={showPassword.confirm ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                aria-pressed={showPassword.confirm}
              >
                {showPassword.confirm ? <FaEye /> : <FaEyeSlash />}
              </button>
            </div>
            {passwordError && <p className="password-error">{passwordError}</p>}
            <div className="password-actions">
              <Button variant="primary" onClick={handleSavePassword}>
                {t('settings.passwordModal.save')}
              </Button>
              <Button variant="secondary" onClick={handleClosePasswordModal}>
                {t('settings.passwordModal.cancel')}
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Modal editar */}
      {modalOpen && (
        <EditModal
          field={editField}
          value={editValue}
          onSave={handleSave}
          onClose={() => setModalOpen(false)}
        />
      )}

      {/* Modal idioma */}
      <Modal isOpen={showLangModal} onClose={() => setShowLangModal(false)} size="sm">
        <h3>{t('settings.language')}</h3>
        {LANGUAGES.map(({ code, label, flag, flagAlt }) => (
          <button
            key={code}
            className={i18n.language === code ? 'lang-modal-btn--active' : ''}
            onClick={() => handleChangeLanguage(code)}
          >
            <img src={flag} alt={flagAlt} className="lang-modal-flag" loading="lazy" />
            <span>{label}</span>
            {i18n.language === code && <span className="lang-modal-check">✓</span>}
          </button>
        ))}
      </Modal>

      {/* ── MODAL ELIMINAR CUENTA ── */}
      <Modal isOpen={showDeleteModal} onClose={() => setShowDeleteModal(false)} size="sm">
        {deleteStep === 'confirm' ? (
          <>
            <h3 style={{ color: '#ff4d5b' }}>⚠️ {t('settings.deleteAccount')}</h3>
            <p
              style={{
                fontSize: 'calc(var(--a11y-font-size) * 0.875)',
                color: 'var(--text-secondary)',
                textAlign: 'center',
                margin: '0 0 4px',
              }}
            >
              {t('settings.deleteAccountConfirm')}
            </p>
            <p
              style={{
                fontSize: 'calc(var(--a11y-font-size) * 0.8125)',
                color: 'var(--text-muted)',
                textAlign: 'center',
                margin: '0 0 12px',
              }}
            >
              {t('settings.deleteAccountDetail')}
            </p>
            <div className="password-actions">
              <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                {t('editModal.cancel')}
              </Button>
              <Button variant="danger-solid" onClick={handleDeleteRequest}>
                {t('settings.deleteBtn')}
              </Button>
            </div>
          </>
        ) : (
          <>
            <h3 style={{ color: 'var(--accent)' }}>✉️ {t('settings.deleteRequestSent')}</h3>
            <Button
              variant="secondary"
              onClick={() => setShowDeleteModal(false)}
              className="mt-2"
            >
              {t('editModal.cancel')}
            </Button>
          </>
        )}
      </Modal>

      {/* Modal ayuda */}
      <Modal isOpen={helpModal.open} onClose={() => setHelpModal({ open: false, type: null })} size="lg">
        {helpModal.type === 'faq' && (
          <>
            <h3>❓ {t('settings.helpFaq')}</h3>
            <div className="help-modal-body">
              {[
                { q: t('settings.faq.q1'), a: t('settings.faq.a1') },
                { q: t('settings.faq.q2'), a: t('settings.faq.a2') },
                { q: t('settings.faq.q3'), a: t('settings.faq.a3') },
                { q: t('settings.faq.q4'), a: t('settings.faq.a4') },
              ].map(({ q, a }) => (
                <div key={q} className="faq-item">
                  <strong>{q}</strong>
                  <p>{a}</p>
                </div>
              ))}
            </div>
          </>
        )}

        {helpModal.type === 'contact' && (
          <>
            <h3>📬 {t('settings.helpContact')}</h3>
            <div className="help-modal-body">
              {[
                {
                  icon: '✉️',
                  title: t('settings.contact.emailTitle'),
                  desc: 'soporte@eduaircontrol.com',
                },
                {
                  icon: '🕐',
                  title: t('settings.contact.scheduleTitle'),
                  desc: t('settings.contact.scheduleDesc'),
                },
                {
                  icon: '⏱️',
                  title: t('settings.contact.responseTitle'),
                  desc: t('settings.contact.responseDesc'),
                },
              ].map(({ icon, title, desc }) => (
                <div key={title} className="contact-item">
                  <span className="contact-icon">{icon}</span>
                  <div>
                    <strong>{title}</strong>
                    <p>{desc}</p>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

        {helpModal.type === 'terms' && (
          <>
            <h3> {t('settings.helpTerms')}</h3>
            <div className="help-modal-body help-modal-scroll">
              <p>{t('settings.terms.intro')}</p>
              {t('settings.terms.sections', { returnObjects: true }).map((section, i) => (
                <div className="policy-section" key={i}>
                  <h4>{section.title}</h4>
                  <ul>
                    {section.items.map((item, j) => (
                      <li key={j}>{item}</li>
                    ))}
                  </ul>
                </div>
              ))}
              <p className="policy-footer">{t('settings.terms.footer')}</p>
            </div>
          </>
        )}

        {helpModal.type === 'privacy' && (
          <>
            <h3>🔒 {t('settings.helpPrivacy')}</h3>
            <div className="help-modal-body help-modal-scroll">
              <p>{t('settings.privacyModal.intro')}</p>
              {t('settings.privacyModal.sections', { returnObjects: true }).map(
                (section, i) => (
                  <div className="policy-section" key={i}>
                    <h4>{section.title}</h4>
                    <ul>
                      {section.items.map((item, j) => (
                        <li key={j}>{item}</li>
                      ))}
                    </ul>
                  </div>
                )
              )}
              <p className="policy-footer">{t('settings.privacyModal.footer')}</p>
            </div>
          </>
        )}

        {helpModal.type === 'version' && (
          <>
            <h3>📱 {t('settings.helpVersion')}</h3>
            <div className="help-modal-body">
              <div className="version-info">
                <div className="version-badge">v1.0.0</div>
                <p>{t('settings.versionDesc')}</p>
                <p className="version-date">{t('settings.versionDate')}</p>
                <div className="version-tags">
                  <span className="version-tag">React 18</span>
                  <span className="version-tag">i18n</span>
                  <span className="version-tag">Dark Mode</span>
                </div>
              </div>
            </div>
          </>
        )}
      </Modal>
    </div>
  );
}

export default SettingsScreen;
