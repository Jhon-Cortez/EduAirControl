export default function calculateEnvironmentScore(environment) {
  let score = 100;

  if (environment.temp > 26 || environment.temp < 20) score -= 10;

  if (environment.humidity > 60 || environment.humidity < 40) score -= 10;

  if (environment.co2 > 800) score -= 20;

  if (environment.co2 > 1000) score -= 20;

  if (environment.noise > 55) score -= 15;

  if (environment.noise > 70) score -= 15;

  return Math.max(score, 0);
}
