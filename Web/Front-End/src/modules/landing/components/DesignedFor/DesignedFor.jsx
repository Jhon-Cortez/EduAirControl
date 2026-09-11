import './DesignedFor.css';
import UserCard from './UserCard';
import { useTranslation } from 'react-i18next';
import { ShieldCheck, GraduationCap, UserRound, Wrench } from 'lucide-react';

function DesignedFor() {
  const { t } = useTranslation();

  const users = [
    {
      icon: ShieldCheck,
      title: t('landing.designedFor.user1Title'),
      description: t('landing.designedFor.user1Desc'),
    },
    {
      icon: GraduationCap,
      title: t('landing.designedFor.user2Title'),
      description: t('landing.designedFor.user2Desc'),
    },
    {
      icon: UserRound,
      title: t('landing.designedFor.user3Title'),
      description: t('landing.designedFor.user3Desc'),
    },
    {
      icon: Wrench,
      title: t('landing.designedFor.user4Title'),
      description: t('landing.designedFor.user4Desc'),
    },
  ];

  return (
    <section id="designed" className="designed-for">
      <span className="section-badge">{t('landing.designedFor.badge')}</span>

      <h2>
        {t('landing.designedFor.title')}
        <span> {t('landing.designedFor.titleHighlight')}</span>
      </h2>

      <p>{t('landing.designedFor.description')}</p>

      <div className="users-grid">
        {users.map((user) => (
          <UserCard key={user.title} {...user} />
        ))}
      </div>
    </section>
  );
}

export default DesignedFor;
