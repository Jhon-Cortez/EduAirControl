package com.eduaircontrol.backend.modules.classrooms.application.page;

import java.util.List;

/**
 * Resultado paginado independiente de la infraestructura (los puertos
 * no deben exponer tipos de Spring Data).
 */
public record PageResult<T>(List<T> content, long total, int page, int limit) {

    public int totalPages() {
        return limit > 0 ? (int) Math.ceil((double) total / limit) : 0;
    }
}
