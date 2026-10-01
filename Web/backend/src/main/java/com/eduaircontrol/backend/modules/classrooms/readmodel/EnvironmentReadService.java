package com.eduaircontrol.backend.modules.classrooms.readmodel;

import com.eduaircontrol.backend.modules.classrooms.application.port.CampusRepository;
import com.eduaircontrol.backend.modules.classrooms.application.port.EnvironmentTypeRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.model.Campus;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EnvironmentType;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.FavoritesPort;
import com.eduaircontrol.backend.shared.contract.MeasurementSnapshotPort;
import com.eduaircontrol.backend.shared.contract.ThresholdPort;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EnvironmentReadService {

    private static final DateTimeFormatter LAST_UPDATE =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH).withZone(ZoneId.systemDefault());

    private final EnvironmentLookupPort environmentLookupPort;
    private final CampusRepository campusRepository;
    private final EnvironmentTypeRepository environmentTypeRepository;
    private final MeasurementSnapshotPort measurementSnapshotPort;
    private final FavoritesPort favoritesPort;
    private final ThresholdPort thresholdPort;
    private final VariableCatalogPort variableCatalogPort;
    private final UserIdentityPort userIdentityPort;

    @Transactional(readOnly = true)
    public List<EnvironmentView> list(String email, String query) {
        List<EnvironmentLookupPort.EnvironmentInfo> environments = filter(environmentLookupPort.findAll(), query);
        return build(environments, email);
    }

    @Transactional(readOnly = true)
    public EnvironmentView get(String email, UUID environmentId) {
        EnvironmentLookupPort.EnvironmentInfo environment = environmentLookupPort.findById(environmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambiente no encontrado"));
        return build(List.of(environment), email).get(0);
    }

    private List<EnvironmentLookupPort.EnvironmentInfo> filter(
            List<EnvironmentLookupPort.EnvironmentInfo> environments, String query) {
        if (query == null || query.isBlank()) {
            return environments;
        }
        String needle = query.trim().toLowerCase(Locale.ROOT);
        return environments.stream()
                .filter(environment -> environment.name().toLowerCase(Locale.ROOT).contains(needle)
                        || environment.code().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    private List<EnvironmentView> build(List<EnvironmentLookupPort.EnvironmentInfo> environments,
                                        String email) {
        if (environments.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = environments.stream().map(EnvironmentLookupPort.EnvironmentInfo::id).toList();

        Map<UUID, Campus> campuses = campusRepository.search(null, null, 1, 1000).content().stream()
                .collect(Collectors.toMap(Campus::getId, Function.identity()));
        Map<UUID, EnvironmentType> types = environmentTypeRepository.search(null, 1, 1000).content().stream()
                .collect(Collectors.toMap(EnvironmentType::getId, Function.identity()));
        Map<UUID, MeasurementSnapshotPort.Snapshot> snapshots =
                measurementSnapshotPort.latestByEnvironments(ids);
        UUID userId = email == null ? null : userIdentityPort.idByEmail(email).orElse(null);
        Set<UUID> favorites = favoritesPort.favoriteIds(userId);
        UUID temperatureId = variableCatalogPort.findByCode("temperature")
                .map(VariableCatalogPort.VariableRef::id)
                .orElse(null);

        Map<UUID, Map<UUID, ThresholdPort.Range>> rangesByEnvironment = new HashMap<>();
        ids.forEach(id -> {
            Map<UUID, ThresholdPort.Range> byVariable = new HashMap<>();
            thresholdPort.warningRanges(id).forEach(range -> byVariable.put(range.variableId(), range));
            rangesByEnvironment.put(id, byVariable);
        });

        return environments.stream()
                .map(environment -> toView(environment, campuses, types, snapshots,
                        favorites, temperatureId, rangesByEnvironment))
                .toList();
    }

    private EnvironmentView toView(EnvironmentLookupPort.EnvironmentInfo environment,
                                   Map<UUID, Campus> campuses,
                                   Map<UUID, EnvironmentType> types,
                                   Map<UUID, MeasurementSnapshotPort.Snapshot> snapshots,
                                   Set<UUID> favorites,
                                   UUID temperatureId,
                                   Map<UUID, Map<UUID, ThresholdPort.Range>> rangesByEnvironment) {
        Campus campus = campuses.get(environment.campusId());
        EnvironmentType type = types.get(environment.environmentTypeId());
        MeasurementSnapshotPort.Snapshot snapshot = snapshots.get(environment.id());
        Map<String, BigDecimal> values = snapshot != null && snapshot.values() != null
                ? snapshot.values()
                : Map.of();
        ThresholdPort.Range temperature = temperatureId == null
                ? null
                : rangesByEnvironment.getOrDefault(environment.id(), Map.of()).get(temperatureId);

        return EnvironmentView.builder()
                .id(environment.id())
                .code(environment.code())
                .name(environment.name())
                .building(campus != null ? campus.getName() : null)
                .location(campus != null ? campus.getName() : null)
                .campusId(environment.campusId())
                .envType(type != null ? type.getName() : null)
                .envTypeId(environment.environmentTypeId())
                .floor(environment.floor())
                .capacity(environment.occupancyCapacity())
                .tempMin(temperature != null ? temperature.min() : null)
                .tempMax(temperature != null ? temperature.max() : null)
                .temp(values.get("temperature"))
                .humidity(values.get("humidity"))
                .co2(values.get("co2"))
                .noise(values.get("noise"))
                .statusKey(snapshot != null && snapshot.statusKey() != null
                        ? snapshot.statusKey()
                        : MeasurementSnapshotPort.STATUS_NORMAL)
                .favorite(favorites.contains(environment.id()))
                .lastUpdate(snapshot != null && snapshot.measuredAt() != null
                        ? LAST_UPDATE.format(snapshot.measuredAt())
                        : null)
                .build();
    }
}
