import { useTranslation } from 'react-i18next';

import { FaUser, FaEnvelope, FaBriefcase, FaPhone, FaMapMarkerAlt, FaCamera } from 'react-icons/fa';
import { IoLogOut } from 'react-icons/io5';
import { MdEdit } from 'react-icons/md';

import { useProfileVM } from '../../../viewmodels';
import { Modal, Button } from '../../../shared/components';
import Navbar from '../../dashboard/components/Navbar/Navbar';
import './Profile.css';

const FIELDS = [
  { key: 'fullName', label: 'profile.fullName', icon: FaUser, type: 'text' },
  { key: 'email', label: 'profile.email', icon: FaEnvelope, type: 'email' },
  { key: 'title', label: 'profile.titleLabel', icon: FaBriefcase, type: 'text' },
  { key: 'phone', label: 'profile.phone', icon: FaPhone, type: 'text' },
  { key: 'location', label: 'profile.location', icon: FaMapMarkerAlt, type: 'text' },
];

function ProfileScreen() {
  const { t } = useTranslation();
  const {
    profile,
    form,
    avatar,
    isEditing,
    logoutModal,
    avatarLoading,
    avatarError,
    fileInputRef,
    setIsEditing,
    setLogoutModal,
    handleChange,
    handleAvatarChange,
    handleRemoveAvatar,
    handleSave,
    handleCancel,
    handleLogout,
    openAvatarPicker,
  } = useProfileVM();

  return (
    <div className="profile-page-final">
      <Navbar />

      <main id="main-content">
      <div className="profile-container-final">
        {/* HERO */}
        <div className={`profile-hero-final ${isEditing ? 'editing' : ''}`}>
          <div
            className={`hero-avatar-wrapper ${isEditing ? 'editable' : ''}`}
            onClick={isEditing ? openAvatarPicker : undefined}
          >
            {avatar ? (
              <img src={avatar} alt="Foto de perfil" className="hero-avatar-img" />
            ) : (
              <div className="hero-avatar-icon">
                <FaUser />
              </div>
            )}

            {avatarLoading && <div className="hero-avatar-loading" />}

            {isEditing && !avatarLoading && (
              <div className="hero-avatar-overlay">
                <FaCamera />
                <span className="hero-avatar-overlay-text">Cambiar foto</span>
              </div>
            )}

            {isEditing && avatar && (
              <button
                className="hero-avatar-overlay-remove"
                onClick={(e) => {
                  e.stopPropagation();
                  handleRemoveAvatar();
                }}
                title="Eliminar foto"
              >
                ✕
              </button>
            )}
          </div>

          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="sr-only"
            onChange={handleAvatarChange}
          />

          {avatarError && <p className="hero-avatar-error">{avatarError}</p>}

          <div className="hero-info-final">
            <h1>{profile.fullName}</h1>
            <p className="hero-title-final">{profile.title}</p>
            <p className="hero-email-final">
              <FaEnvelope />
              {profile.email}
            </p>
            <span className="hero-location-final">
              <FaMapMarkerAlt />
              {profile.location}
            </span>
          </div>

          {!isEditing ? (
            <Button variant="primary" icon={<MdEdit />} onClick={() => setIsEditing(true)}>
              {t('profile.update')}
            </Button>
          ) : (
            <div className="hero-actions-final">
              <Button variant="primary" onClick={handleSave}>
                {t('profile.save')}
              </Button>
              <Button variant="secondary" onClick={handleCancel}>
                {t('profile.cancel')}
              </Button>
            </div>
          )}
        </div>

        {/* INFORMACIÓN */}
        <div className="profile-info-final">
          <h2>{t('profile.title')}</h2>
          <p className="info-description">{t('profile.description')}</p>

          <div className="info-items-container">
            {FIELDS.map((field) => (
              <div key={field.key} className="info-item">
                <div className="item-icon">
                  <field.icon />
                </div>
                <div className="item-content">
                  <label className="item-label">{t(field.label)}</label>
                  <input
                    className="item-input"
                    type={field.type}
                    disabled={!isEditing}
                    value={form[field.key]}
                    onChange={(e) => handleChange(field.key, e.target.value)}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* LOGOUT */}
        <div className="profile-footer-final">
          <Button variant="danger" icon={<IoLogOut />} onClick={() => setLogoutModal(true)}>
            {t('profile.logoutBtn')}
          </Button>
        </div>
      </div>
      </main>

      {/* MODAL LOGOUT */}
      <Modal isOpen={logoutModal} onClose={() => setLogoutModal(false)} size="sm">
        <div className="logout-modal-header">
          <IoLogOut size={30} color="#ff4d5b" />
          <h3>{t('profile.logoutTitle')}</h3>
        </div>
        <p className="logout-modal-message">{t('profile.logoutMessage')}</p>
        <div className="modal-actions-final">
          <Button variant="secondary" onClick={() => setLogoutModal(false)}>
            {t('profile.cancel', 'Cancelar')}
          </Button>
          <Button variant="danger-solid" onClick={handleLogout}>
            {t('profile.logoutBtn')}
          </Button>
        </div>
      </Modal>
    </div>
  );
}

export default ProfileScreen;
