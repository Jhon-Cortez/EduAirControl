import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

import { FaUser, FaEnvelope, FaBriefcase, FaPhone, FaMapMarkerAlt } from 'react-icons/fa';

import { IoLogOut } from 'react-icons/io5';
import { MdEdit } from 'react-icons/md';

import Navbar from '../../dashboard/components/Navbar/Navbar';
import './Profile.css';

const DEFAULT_PROFILE = {
  fullName: 'Maria de los Angeles Olaya Garcia',
  email: 'mariadelosangelesolayagar@gmail.com',
  title: 'Product Manager',
  phone: '+57 322 9523486',
  location: 'Neiva, Colombia',
};

function ProfileScreen() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [profile, setProfile] = useState(
    () => JSON.parse(localStorage.getItem('profile')) || DEFAULT_PROFILE
  );

  const [form, setForm] = useState(profile);

  const [isEditing, setIsEditing] = useState(false);

  const [logoutModal, setLogoutModal] = useState(false);

  const fields = [
    {
      key: 'fullName',
      label: t('profile.fullName'),
      icon: <FaUser />,
      type: 'text',
    },
    {
      key: 'email',
      label: t('profile.email'),
      icon: <FaEnvelope />,
      type: 'email',
    },
    {
      key: 'title',
      label: t('profile.titleLabel'),
      icon: <FaBriefcase />,
      type: 'text',
    },
    {
      key: 'phone',
      label: t('profile.phone'),
      icon: <FaPhone />,
      type: 'text',
    },
    {
      key: 'location',
      label: t('profile.location'),
      icon: <FaMapMarkerAlt />,
      type: 'text',
    },
  ];

  const handleChange = (field, value) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSave = () => {
    setProfile(form);
    localStorage.setItem('profile', JSON.stringify(form));
    setIsEditing(false);
  };

  const handleCancel = () => {
    setForm(profile);
    setIsEditing(false);
  };

  const handleLogout = () => {
    setLogoutModal(false);
    navigate('/');
  };

  return (
    <div className="profile-page-final">
      <Navbar />

      <div className="profile-container-final">
        {/* HERO */}

        <div className="profile-hero-final">
          <div className="hero-avatar-final">
            <FaUser />
          </div>

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
            <button className="btn-edit-profile-final" onClick={() => setIsEditing(true)}>
              <MdEdit />
              {t('profile.update')}
            </button>
          ) : (
            <div className="hero-actions-final">
              <button className="btn-save-final" onClick={handleSave}>
                {t('profile.save')}
              </button>

              <button className="btn-cancel-final" onClick={handleCancel}>
                {t('profile.cancel')}
              </button>
            </div>
          )}
        </div>

        {/* INFORMACIÓN */}

        <div className="profile-info-final">
          <h2>{t('profile.title')}</h2>

          <p className="info-description">{t('profile.description')}</p>

          <div className="info-items-container">
            {fields.map((field) => (
              <div key={field.key} className="info-item">
                <div className="item-icon">{field.icon}</div>

                <div className="item-content">
                  <label className="item-label">{field.label}</label>

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
          <button className="btn-logout-final" onClick={() => setLogoutModal(true)}>
            <IoLogOut />
            {t('profile.logoutBtn')}
          </button>
        </div>
      </div>

      {/* MODAL LOGOUT */}

      {logoutModal && (
        <div className="modal-overlay-final" onClick={() => setLogoutModal(false)}>
          <div className="modal-final modal-logout" onClick={(e) => e.stopPropagation()}>
            <div className="logout-modal-header">
              <IoLogOut size={30} color="#ff4d5b" />

              <h3>{t('profile.logoutTitle')}</h3>
            </div>

            <p className="logout-modal-message">{t('profile.logoutMessage')}</p>

            <div className="modal-actions-final">
              <button className="btn-cancel-final" onClick={() => setLogoutModal(false)}>
                {t('profile.cancel', 'Cancelar')}
              </button>

              <button className="btn-logout-confirm-final" onClick={handleLogout}>
                {t('profile.logoutBtn')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProfileScreen;
