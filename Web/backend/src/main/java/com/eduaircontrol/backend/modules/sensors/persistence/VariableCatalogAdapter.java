package com.eduaircontrol.backend.modules.sensors.persistence;

import com.eduaircontrol.backend.modules.sensors.entity.Variable;
import com.eduaircontrol.backend.modules.sensors.repository.VariableRepository;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VariableCatalogAdapter implements VariableCatalogPort {

    private final VariableRepository variableRepository;

    @Override
    public Optional<VariableRef> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return variableRepository.findByCode(code.trim().toLowerCase()).map(VariableCatalogAdapter::toRef);
    }

    @Override
    public List<VariableRef> findAll() {
        return variableRepository.findAll().stream().map(VariableCatalogAdapter::toRef).toList();
    }

    @Override
    public Optional<VariableRef> findById(UUID variableId) {
        if (variableId == null) {
            return Optional.empty();
        }
        return variableRepository.findById(variableId).map(VariableCatalogAdapter::toRef);
    }

    private static VariableRef toRef(Variable variable) {
        return new VariableRef(variable.getId(), variable.getCode(), variable.getName());
    }
}
