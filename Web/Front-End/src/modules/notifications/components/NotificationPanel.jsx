import './NotificationPanel.css';
import { useTranslation } from 'react-i18next';
import { useEffect, useRef } from 'react';
import { useNotifications } from '../hooks/useNotifications';

function NotificationPanel({ isOpen, onClose }) {
  const { notifications, unreadCount, markAsRead, markAllRead } = useNotifications();

  const { t } = useTranslation();
  const panelRef = useRef(null);

  useEffect(() => {
    if (!isOpen) return;

    const handleClickOutside = (e) => {
      if (panelRef.current && !panelRef.current.contains(e.target)) {
        onClose();
      }
    };

    const handleEsc = (e) => {
      if (e.key === 'Escape') onClose();
    };

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleEsc);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleEsc);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="notif-overlay">
      <div className="notification-panel" ref={panelRef}>
        {/* HEADER */}

        <div className="notification-header">
          <div className="notification-header-info">
            <div className="notification-header-top">
              <h3>{t('notifications.title')}</h3>

              {unreadCount > 0 && <span className="notification-badge">{unreadCount}</span>}
            </div>

            {unreadCount > 0 && (
              <span className="mark-read-link" onClick={markAllRead}>
                ✓ {t('notifications.markAllRead')}
              </span>
            )}
          </div>

          <button className="close-btn" onClick={onClose}>
            ✕
          </button>
        </div>

        {/* LISTA */}

        <div className="notification-list">
          {notifications.length === 0 ? (
            <div className="notification-empty">
              <div className="notification-empty-icon">🔔</div>

              <p>{t('notifications.empty')}</p>
            </div>
          ) : (
            notifications.map((notification) => (
              <div
                key={notification.id}
                className={`notification-card ${notification.type} ${
                  !notification.read ? 'unread' : ''
                }`}
                onClick={() => markAsRead(notification.id)}
              >
                {/* ICONO */}

                <div className="notification-icon">
                  {notification.type === 'danger' && '🚨'}

                  {notification.type === 'warning' && '⚠️'}

                  {notification.type === 'info' && 'ℹ️'}
                </div>

                {/* CONTENIDO */}

                <div className="notification-content">
                  <div className="notification-title-row">
                    <strong>{t(notification.title)}</strong>

                    {!notification.read && <span className="notification-dot"></span>}
                  </div>

                  <p>{t(notification.message, notification.data)}</p>

                  <span className="time">
                    {notification.time
                      ? new Date(notification.time).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit',
                        })
                      : ''}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}

export default NotificationPanel;
