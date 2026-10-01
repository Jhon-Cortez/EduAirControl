package com.eduaircontrol.backend.modules.classrooms.persistence;

import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.application.port.EducationalEnvironmentRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EducationalEnvironmentRepositoryAdapter implements EducationalEnvironmentRepository {

    private final EducationalEnvironmentJpaRepository jpaRepository;
    private final CampusJpaRepository campusJpaRepository;
    private final EnvironmentTypeJpaRepository environmentTypeJpaRepository;

    @Override
    public EducationalEnvironment save(EducationalEnvironment environment) {
        return jpaRepository.save(environment);
    }

    @Override
    public Optional<EducationalEnvironment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByCampusAndCode(UUID campusId, String code) {
        return jpaRepository.existsByCampusIdAndCode(campusId, code);
    }

    @Override
    public boolean existsActiveCampus(UUID campusId) {
        return campusJpaRepository.existsByIdAndDeletedAtIsNull(campusId);
    }

    @Override
    public boolean existsActiveEnvironmentType(UUID environmentTypeId) {
        return environmentTypeJpaRepository.existsByIdAndDeletedAtIsNull(environmentTypeId);
    }

    @Override
    public PageResult<EducationalEnvironment> search(String query, RecordStatus status,
            UUID campusId, UUID environmentTypeId, int page, int limit) {
        Specification<EducationalEnvironment> specification = (root, q, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (campusId != null) {
                predicates.add(cb.equal(root.get("campusId"), campusId));
            }
            if (environmentTypeId != null) {
                predicates.add(cb.equal(root.get("environmentTypeId"), environmentTypeId));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        Page<EducationalEnvironment> result = jpaRepository.findAll(
                specification,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.ASC, "code")));
        return new PageResult<>(result.getContent(), result.getTotalElements(), page, limit);
    }
}
