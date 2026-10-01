package com.eduaircontrol.backend.modules.classrooms.domain.model;

/**
 * Ciclo de vida controlado de los registros con estado (§6 del dominio).
 * La soft delete (deleted_at) es independiente de este estado.
 */
public enum RecordStatus {
    ACTIVE,
    INACTIVE
}
