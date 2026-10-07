import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { FaUsers, FaBuilding, FaUserShield, FaCheckCircle, FaTimesCircle } from 'react-icons/fa';
import { HiOutlineBuildingOffice2 } from 'react-icons/hi2';
import Navbar from '../../dashboard/components/Navbar/Navbar';
import apiClient from '../../../shared/services/apiClient';
import './AdminScreen.css';

function AdminScreen() {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState('users');
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [usersData, rolesData] = await Promise.all([
        apiClient.get('/admin/users'),
        apiClient.get('/admin/roles'),
      ]);
      setUsers(usersData);
      setRoles(rolesData);
    } catch (err) {
      setError(err.message || 'Error loading data');
    } finally {
      setLoading(false);
    }
  };

  const handleAssignRole = async (userId, roleName) => {
    setError('');
    setSuccess('');
    try {
      const user = users.find((u) => u.userId === userId);
      const currentRoles = user?.roles || [];
      const newRoles = currentRoles.includes(roleName)
        ? currentRoles.filter((r) => r !== roleName)
        : [...currentRoles, roleName];
      await apiClient.put(`/admin/users/${userId}/roles`, { roleNames: newRoles });
      setSuccess(t('admin.roleUpdated', 'Roles updated successfully'));
      loadData();
    } catch (err) {
      setError(err.message || 'Error updating roles');
    }
  };

  return (
    <div className="admin-page">
      <Navbar />
      <div className="admin-container">
        <header className="admin-header">
          <div className="admin-header-icon">
            <FaUserShield />
          </div>
          <div>
            <h1>{t('admin.title', 'Panel de Administración')}</h1>
            <p>{t('admin.subtitle', 'Gestiona usuarios, roles e instituciones')}</p>
          </div>
        </header>

        {error && <div className="admin-alert admin-alert-error">⚠ {error}</div>}
        {success && <div className="admin-alert admin-alert-success">✓ {success}</div>}

        <div className="admin-tabs">
          <button
            className={`admin-tab ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            <FaUsers /> {t('admin.usersTab', 'Usuarios')}
          </button>
          <button
            className={`admin-tab ${activeTab === 'institutions' ? 'active' : ''}`}
            onClick={() => setActiveTab('institutions')}
          >
            <FaBuilding /> {t('admin.institutionsTab', 'Instituciones')}
          </button>
        </div>

        {activeTab === 'users' && (
          <section className="admin-section">
            <h2>{t('admin.usersTitle', 'Gestión de Usuarios')}</h2>
            <p className="admin-hint">
              {t('admin.usersHint', 'Asigna el rol de Administrador a los usuarios que serán administradores de sus compañías')}
            </p>
            {loading ? (
              <p className="admin-loading">...</p>
            ) : (
              <div className="admin-table-wrapper">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>{t('admin.colEmail', 'Correo')}</th>
                      <th>{t('admin.colUsername', 'Usuario')}</th>
                      <th>{t('admin.colRoles', 'Roles')}</th>
                      <th>{t('admin.colActions', 'Acciones')}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((user) => (
                      <tr key={user.userId}>
                        <td>{user.email}</td>
                        <td>{user.username}</td>
                        <td>
                          <div className="admin-roles">
                            {(user.roles || []).map((role) => (
                              <span key={role} className={`admin-role-badge admin-role-${role.toLowerCase()}`}>
                                {role}
                              </span>
                            ))}
                          </div>
                        </td>
                        <td>
                          <div className="admin-actions">
                            {roles
                              .filter((r) => r.name !== 'SUPER_ADMIN')
                              .map((role) => (
                                <button
                                  key={role.name}
                                  className={`admin-role-btn ${
                                    (user.roles || []).includes(role.name) ? 'active' : ''
                                  }`}
                                  onClick={() => handleAssignRole(user.userId, role.name)}
                                  title={role.description}
                                >
                                  {(user.roles || []).includes(role.name) ? (
                                    <FaCheckCircle />
                                  ) : (
                                    <FaTimesCircle />
                                  )}
                                  {role.name}
                                </button>
                              ))}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        )}

        {activeTab === 'institutions' && (
          <section className="admin-section">
            <h2>
              <HiOutlineBuildingOffice2 /> {t('admin.institutionsTitle', 'Gestión de Instituciones')}
            </h2>
            <p className="admin-hint">
              {t('admin.institutionsHint', 'Crea y administra las instituciones (compañías) del sistema')}
            </p>
            <div className="admin-placeholder">
              <p>{t('admin.institutionsPlaceholder', 'CRUD de instituciones próximamente')}</p>
            </div>
          </section>
        )}
      </div>
    </div>
  );
}

export default AdminScreen;
