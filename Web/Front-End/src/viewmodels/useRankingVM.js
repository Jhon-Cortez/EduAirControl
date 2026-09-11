import { useEffect, useMemo, useState } from 'react';
import rankingService from '../modules/ranking/services/rankingService';

export default function useRankingVM() {
  const [ranking, setRanking] = useState([]);

  const [filters, setFilters] = useState({
    name: '',
    building: '',
    status: '',
  });

  useEffect(() => {
    async function loadRanking() {
      const data = await rankingService.getRanking();

      setRanking(data);
    }

    loadRanking();
  }, []);

  const filteredRanking = useMemo(() => {
    return ranking
      .filter((environment) => {
        const matchName = environment.name.toLowerCase().includes(filters.name.toLowerCase());

        const matchBuilding = !filters.building || environment.locationKey === filters.building;

        const matchStatus = !filters.status || environment.status === filters.status;

        return matchName && matchBuilding && matchStatus;
      })
      .sort((a, b) => b.score - a.score)
      .map((environment, index) => ({
        ...environment,
        position: index + 1,
      }));
  }, [ranking, filters]);

  const statistics = useMemo(() => {
    const average =
      ranking.length === 0
        ? 0
        : Math.round(ranking.reduce((sum, env) => sum + env.score, 0) / ranking.length);

    const ordered = [...ranking].sort((a, b) => b.score - a.score);

    return {
      total: ranking.length,

      average,

      best: ordered[0],

      worst: ordered[ordered.length - 1],
    };
  }, [ranking]);

  const suggestions = useMemo(
    () => ({
      buildings: [...new Set(ranking.map((e) => e.locationKey))],

      status: [...new Set(ranking.map((e) => e.status))],
    }),
    [ranking]
  );

  return {
    ranking: filteredRanking,

    filters,

    setFilters,

    suggestions,

    statistics,
  };
}
