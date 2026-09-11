export function getRecommendations(environment, t) {
    const recommendations = [];

    if (environment.temp > 27) {
        recommendations.push(t("allEnvironments.recTemp"));
    }

    if (environment.humidity > 65) {
        recommendations.push(t("allEnvironments.recHumidity"));
    }

    if (environment.co2 > 1000) {
        recommendations.push(t("allEnvironments.recCo2"));
    }

    if (environment.noise > 65) {
        recommendations.push(t("allEnvironments.recNoise"));
    }

    if (recommendations.length === 0) {
        recommendations.push(t("allEnvironments.recPerfect"));
    }

    return recommendations;
}