package com.eduaircontrol.backend.modules.classrooms.application;

import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.application.port.EducationalEnvironmentRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.exception.ConflictException;
import com.eduaircontrol.backend.modules.classrooms.domain.exception.NotFoundException;
import com.eduaircontrol.backend.modules.classrooms.domain.exception.ValidationException;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationalEnvironmentService {

    private final EducationalEnvironmentRepository environmentRepository;

    @Transactional(readOnly = true)
    public PageResult<EducationalEnvironment> list(String query, RecordStatus status,
            UUID campusId, UUID environmentTypeId, int page, int limit) {
        return environmentRepository.search(query, status, campusId, environmentTypeId, page, limit);
    }

    @Transactional(readOnly = true)
    public EducationalEnvironment get(UUID id) {
        return environmentRepository.findById(id)
                .filter(environment -> !environment.isDeleted())
                .orElseThrow(() -> new NotFoundException("Educational environment not found: " + id));
    }

    public EducationalEnvironment create(UUID campusId, String code, String name,
            UUID environmentTypeId, Integer floor, BigDecimal areaM2,
            Integer occupancyCapacity, RecordStatus status) {
        String normalizedCode = CampusService.requireText(code, "code").toUpperCase();
        requireReferences(campusId, environmentTypeId);
        if (environmentRepository.existsByCampusAndCode(campusId, normalizedCode)) {
            throw new ConflictException("Environment code already exists in this campus: " + normalizedCode);
        }
        EducationalEnvironment environment = EducationalEnvironment.builder()
                .campusId(campusId)
                .code(normalizedCode)
                .name(CampusService.requireText(name, "name"))
                .environmentTypeId(environmentTypeId)
                .floor(floor)
                .areaM2(areaM2)
                .occupancyCapacity(occupancyCapacity)
                .status(status != null ? status : RecordStatus.ACTIVE)
                .build();
        return environmentRepository.save(environment);
    }

    public EducationalEnvironment update(UUID id, UUID campusId, String code, String name,
            UUID environmentTypeId, Integer floor, BigDecimal areaM2,
            Integer occupancyCapacity, RecordStatus status) {
        EducationalEnvironment environment = get(id);
        UUID targetCampus = campusId != null ? campusId : environment.getCampusId();
        if (campusId != null || code != null) {
            requireCampus(targetCampus);
        }
        if (campusId != null) {
            environment.setCampusId(campusId);
        }
        if (code != null) {
            String normalizedCode = CampusService.requireText(code, "code").toUpperCase();
            if (!environment.getCode().equals(normalizedCode)
                    && environmentRepository.existsByCampusAndCode(targetCampus, normalizedCode)) {
                throw new ConflictException("Environment code already exists in this campus: " + normalizedCode);
            }
            environment.setCode(normalizedCode);
        }
        if (name != null) {
            environment.setName(CampusService.requireText(name, "name"));
        }
        if (environmentTypeId != null) {
            requireEnvironmentType(environmentTypeId);
            environment.setEnvironmentTypeId(environmentTypeId);
        }
        if (floor != null) {
            environment.setFloor(floor);
        }
        if (areaM2 != null) {
            environment.setAreaM2(areaM2);
        }
        if (occupancyCapacity != null) {
            environment.setOccupancyCapacity(occupancyCapacity);
        }
        if (status != null) {
            environment.setStatus(status);
        }
        return environmentRepository.save(environment);
    }

    public void delete(UUID id) {
        EducationalEnvironment environment = get(id);
        environment.softDelete();
        environmentRepository.save(environment);
    }

    private void requireReferences(UUID campusId, UUID environmentTypeId) {
        if (campusId == null) {
            throw new ValidationException("campusId is required");
        }
        if (environmentTypeId == null) {
            throw new ValidationException("environmentTypeId is required");
        }
        requireCampus(campusId);
        requireEnvironmentType(environmentTypeId);
    }

    private void requireCampus(UUID campusId) {
        if (!environmentRepository.existsActiveCampus(campusId)) {
            throw new ValidationException("campusId does not reference an existing campus: " + campusId);
        }
    }

    private void requireEnvironmentType(UUID environmentTypeId) {
        if (!environmentRepository.existsActiveEnvironmentType(environmentTypeId)) {
            throw new ValidationException(
                    "environmentTypeId does not reference an existing environment type: " + environmentTypeId);
        }
    }
}
