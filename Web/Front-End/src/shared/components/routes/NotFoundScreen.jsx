import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { IoHomeOutline, IoSearchOutline } from 'react-icons/io5';
import './NotFoundScreen.css';

function NotFoundScreen() {
  const { t } = useTranslation();

  return (
    <main className="notfound" id="main-content">
      <p className="notfound__code">404</p>
      <IoSearchOutline className="notfound__icon" aria-hidden="true" />
      <h1 className="notfound__title">{t('notFound.title', 'Página no encontrada')}</h1>
      <p className="notfound__description">
        {t('notFound.description', 'La ruta que buscas no existe o fue movida.')}
      </p>
      <Link className="notfound__link" to="/landing">
        <IoHomeOutline aria-hidden="true" />
        {t('notFound.backHome', 'Volver al inicio')}
      </Link>
    </main>
  );
}

export default NotFoundScreen;
