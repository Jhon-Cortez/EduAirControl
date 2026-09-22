import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import profileService from '../services/profileService';
import authService from '../../auth/services/authService';

const MAX_IMAGE_SIZE = 2 * 1024 * 1024;

export function useProfileVM() {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  const [profile, setProfile] = useState({ fullName: '', email: '', title: '', phone: '', location: '', avatar: null });
  const [form, setForm] = useState({ fullName: '', email: '', title: '', phone: '', location: '', avatar: null });
  const [avatar, setAvatar] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [logoutModal, setLogoutModal] = useState(false);
  const [avatarLoading, setAvatarLoading] = useState(false);
  const [avatarError, setAvatarError] = useState(null);

  useEffect(() => {
    profileService.get().then((data) => {
      setProfile(data);
      setForm(data);
      setAvatar(data.avatar || null);
    });
  }, []);

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setAvatarError(null);

    if (file.size > MAX_IMAGE_SIZE) {
      setAvatarError('La imagen no debe superar 2 MB');
      e.target.value = '';
      return;
    }

    setAvatarLoading(true);
    const reader = new FileReader();
    reader.onloadend = () => {
      setAvatar(reader.result);
      setAvatarLoading(false);
    };
    reader.onerror = () => {
      setAvatarLoading(false);
      setAvatarError('Error al leer la imagen');
    };
    reader.readAsDataURL(file);
    e.target.value = '';
  };

  const handleRemoveAvatar = () => {
    setAvatar(null);
    setAvatarError(null);
  };

  const handleSave = () => {
    const updated = { ...form, avatar };
    setProfile(updated);
    profileService.save(updated);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setForm(profile);
    setAvatar(profile.avatar || null);
    setIsEditing(false);
    setAvatarError(null);
  };

  const handleLogout = () => {
    authService.logout();
    setLogoutModal(false);
    navigate('/landing');
  };

  const openAvatarPicker = () => {
    fileInputRef.current?.click();
  };

  return {
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
  };
}
