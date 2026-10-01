package com.eduaircontrol.backend.modules.classrooms.persistence;

import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.application.port.EnvironmentTypeRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EnvironmentType;
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
public class EnvironmentTypeRepositoryAdapter implements EnvironmentTypeRepository {

    private final EnvironmentTypeJpaRepository jpaRepository;

    @Override
    public EnvironmentType save(EnvironmentType environmentType) {
        return jpaRepository.save(environmentType);
    }

    @Override
    public Optional<EnvironmentType> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.exists(Specification.<EnvironmentType>where(
                (root, query, cb) -> cb.equal(cb.lower(root.get("code")), code.toLowerCase())));
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.exists(Specification.<EnvironmentType>where(
                (root, query, cb) -> cb.equal(cb.lower(root.get("name")), name.toLowerCase())));
    }

    @Override
    public Optional<EnvironmentType> findByNameIgnoreCase(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return jpaRepository.findAll(Specification.<EnvironmentType>where(
                        (root, query, cb) -> cb.equal(cb.lower(root.get("name")), name.trim().toLowerCase())))
                .stream()
                .filter(type -> type.getDeletedAt() == null)
                .findFirst();
    }

    @Override
    public PageResult<EnvironmentType> search(String query, int page, int limit) {
        Specification<EnvironmentType> specification = (root, q, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        Page<EnvironmentType> result = jpaRepository.findAll(
                specification,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.ASC, "code")));
        return new PageResult<>(result.getContent(), result.getTotalElements(), page, limit);
    }
}
