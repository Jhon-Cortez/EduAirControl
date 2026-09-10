import { useState } from "react";
import { useTranslation } from "react-i18next";
import { FaHeart, FaRegHeart } from "react-icons/fa";

import EnvironmentModal from "../EnvironmentModal/EnvironmentModal";
import ScoreCircle from "../ScoreCircle/ScoreCircle";
import MetricCard from "../MetricCard/MetricCard";

import calculateEnvironmentScore from "../../utils/calculateEnvironmentScore";
import getMetricStatus from "../../utils/getMetricStatus";
import { getEnvironmentStatus } from "../../utils/getEnvironmentStatus";
import { METRIC_DEFINITIONS } from "../../constants/metricDefinitions";

import "./EnvironmentSummaryCard.css";

function EnvironmentSummaryCard({
  environment,
  onToggleFavorite,
}) {

  const { t } = useTranslation();

  const [showModal, setShowModal] = useState(false);

  const isFavorite = environment.isFavorite ?? false;

    const handleFavorite = (e) => {
        e.stopPropagation();

        onToggleFavorite?.(
            environment.id,
            !isFavorite
        );
    };

  const status = getEnvironmentStatus(
    environment.statusKey,
    t
  );

  const score = calculateEnvironmentScore(
    environment
  );

  // Ciclo forEach: recorre la config reutilizable de métricas y, por
  // cada una, toma el dato correspondiente de "environment" (lo que
  // llega desde la API/contexto) para construir la tarjeta.
  const metricCards = [];
  METRIC_DEFINITIONS.forEach((metric) => {
    metricCards.push(
      <MetricCard
        key={metric.key}
        icon={metric.icon}
        label={t(metric.labelKey)}
        value={metric.getValue(environment)}
        status={getMetricStatus(metric.statusType, metric.getRaw(environment), t)}
      />
    );
  });

  return (

    <>

      <div
        className="summary-card"
        onClick={() => setShowModal(true)}
      >

        <div
          className="summary-card-status-bar"
          style={{
            backgroundColor: status.color,
          }}
        />

        <div className="summary-card-header">

          <div className="summary-card-header-left">

            <h3>

              {
                environment.nameKey
                  ? t(environment.nameKey)
                  : environment.name
              }

            </h3>

            <span
              className="summary-status"
              style={{
                color: status.color,
                backgroundColor: status.bg,
              }}
            >

              {status.text}

            </span>

          </div>

          <button
            className={`btn-favorite ${
              isFavorite ? "active" : ""
            }`}
            onClick={handleFavorite}
          >

            {
              isFavorite
                ? <FaHeart />
                : <FaRegHeart />
            }

          </button>

        </div>

        <div className="summary-score">

          <ScoreCircle score={score} />

          <div className="summary-score-info">

            <h4>

              {t("allEnvironments.qualityTitle")}

            </h4>

            <p>

              {t("allEnvironments.qualityDesc")}

            </p>

          </div>

        </div>

        <div className="summary-card-grid">

          {metricCards}

        </div>

      </div>

      <EnvironmentModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        environment={{
            ...environment,
            score,
            isFavorite
        }}
        isFavorite={isFavorite}
        onToggleFavorite={() => {
          onToggleFavorite?.(environment.id, !isFavorite);
        }}
    />

    </>

  );

}

export default EnvironmentSummaryCard;