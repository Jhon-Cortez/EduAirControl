import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';

import { formatDate, getDateFormat } from '../../../../shared/hooks/useDateFormat';

function getActiveTimezone() {
  const auto = JSON.parse(localStorage.getItem('autoTimezone')) ?? true;

  if (auto) {
    return Intl.DateTimeFormat().resolvedOptions().timeZone;
  }

  return localStorage.getItem('manualTimezone') || Intl.DateTimeFormat().resolvedOptions().timeZone;
}

function NavbarInfo({ role }) {
  const { t } = useTranslation();

  const [time, setTime] = useState(new Date());

  const [dateFormat, setDateFormat] = useState(getDateFormat());

  const [timezone, setTimezone] = useState(getActiveTimezone());

  /* ===============================
      RELOJ
  =============================== */

  useEffect(() => {
    const interval = setInterval(() => {
      setTime(new Date());
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  /* ===============================
      CAMBIOS DE CONFIGURACIÓN
  =============================== */

  useEffect(() => {
    const handleDate = (e) => {
      setDateFormat(e.detail);
    };

    const handleTimezone = (e) => {
      setTimezone(e.detail);
    };

    window.addEventListener('dateFormatChanged', handleDate);

    window.addEventListener('timezoneChanged', handleTimezone);

    return () => {
      window.removeEventListener('dateFormatChanged', handleDate);

      window.removeEventListener('timezoneChanged', handleTimezone);
    };
  }, []);

  /* ===============================
      FECHA Y HORA
  =============================== */

  const fecha = formatDate(time, dateFormat);

  const hora = time.toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    timeZone: timezone,
  });

  return (
    <div className="dashboard-navbar-info">
      <span className="dashboard-navbar-info-greeting">
        {t('nav.greeting')} {role === 'admin' ? t('nav.admin') : t('nav.user')}
      </span>

      <span className="dashboard-navbar-info-time">
        {fecha} · {hora}
      </span>
    </div>
  );
}

export default NavbarInfo;
