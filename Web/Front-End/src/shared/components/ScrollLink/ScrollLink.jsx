import { useNavigate, useLocation } from 'react-router-dom';

function ScrollLink({ to, children, className, onClick }) {
  const navigate = useNavigate();
  const location = useLocation();

  const handleClick = (e) => {
    e.preventDefault();

    if (onClick) {
      onClick();
    }

    if (location.pathname !== '/landing') {
      navigate('/landing', {
        state: {
          scrollTo: to,
        },
      });

      return;
    }

    const section = document.getElementById(to);

    if (section) {
      section.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      });
    }
  };

  return (
    <a href={`#${to}`} className={className} onClick={handleClick}>
      {children}
    </a>
  );
}

export default ScrollLink;
