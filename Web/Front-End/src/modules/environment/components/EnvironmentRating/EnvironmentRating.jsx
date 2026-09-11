import { useState } from 'react';
import { FaStar, FaRegStar } from 'react-icons/fa';
import { useTranslation } from 'react-i18next';
import './EnvironmentRating.css';

function EnvironmentRating({ environmentId, initialRating = 0, onRate }) {
  const { t } = useTranslation();
  const [rating, setRating] = useState(initialRating);
  const [hovered, setHovered] = useState(0);
  const [submitted, setSubmitted] = useState(false);

  const labels = {
    1: t('environmentRating.star1'),
    2: t('environmentRating.star2'),
    3: t('environmentRating.star3'),
    4: t('environmentRating.star4'),
    5: t('environmentRating.star5'),
  };

  const handleClick = (value) => {
    setRating(value);
    setSubmitted(true);
    onRate?.(environmentId, value);
  };

  const activeValue = hovered || rating;

  return (
    <div className="environment-rating">
      <h3>{t('environmentRating.title')}</h3>
      <p className="environment-rating-desc">{t('environmentRating.description')}</p>

      <div
        className="environment-rating-stars"
        role="radiogroup"
        aria-label={t('environmentRating.yourRating')}
      >
        {[1, 2, 3, 4, 5].map((value) => (
          <button
            key={value}
            type="button"
            className="star-btn"
            role="radio"
            aria-checked={rating === value}
            aria-label={labels[value]}
            onMouseEnter={() => setHovered(value)}
            onMouseLeave={() => setHovered(0)}
            onClick={() => handleClick(value)}
          >
            {value <= activeValue ? <FaStar /> : <FaRegStar />}
          </button>
        ))}
      </div>

      {activeValue > 0 && <span className="environment-rating-label">{labels[activeValue]}</span>}

      {submitted && <p className="environment-rating-thanks">{t('environmentRating.thankYou')}</p>}
    </div>
  );
}

export default EnvironmentRating;
