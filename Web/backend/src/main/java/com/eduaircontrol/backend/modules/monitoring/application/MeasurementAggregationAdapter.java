package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.repository.MeasurementAggregationDao;
import com.eduaircontrol.backend.shared.contract.MeasurementAggregationPort;
import com.eduaircontrol.backend.shared.contract.ThresholdPort;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Adaptador del contrato de agregacion. Resuelve el umbral de advertencia vigente
 * con la misma precedencia que el resto del dominio (ambiente sobre tipo de ambiente)
 * y delega la agregacion en la proyeccion SQL.
 */
@Service
@RequiredArgsConstructor
public class MeasurementAggregationAdapter implements MeasurementAggregationPort {

    private final MeasurementAggregationDao aggregationDao;
    private final ThresholdPort thresholdPort;

    @Override
    public List<Aggregate> aggregate(UUID environmentId, Instant start, Instant end) {
        Map<UUID, MeasurementAggregationDao.ThresholdRange> ranges = warningRanges(environmentId);
        return aggregationDao.aggregate(environmentId, start, end, ranges);
    }

    private Map<UUID, MeasurementAggregationDao.ThresholdRange> warningRanges(UUID environmentId) {
        Map<UUID, MeasurementAggregationDao.ThresholdRange> ranges = new LinkedHashMap<>();
        for (ThresholdPort.Range range : thresholdPort.warningRanges(environmentId)) {
            ranges.put(range.variableId(), new MeasurementAggregationDao.ThresholdRange(
                    range.min(), range.max()));
        }
        return ranges;
    }
}