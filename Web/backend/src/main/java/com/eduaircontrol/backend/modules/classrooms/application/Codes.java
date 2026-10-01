package com.eduaircontrol.backend.modules.classrooms.application;

import java.text.Normalizer;

/**
 * Generacion de codigos legibles y unicos a partir de nombres libres
 * (los formularios del frontend no envian code, solo name/location).
 */
public final class Codes {

    private Codes() {
    }

    public static String slug(String value) {
        if (value == null || value.isBlank()) {
            return "CODIGO";
        }
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = withoutAccents.toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "CODIGO" : slug;
    }

    /**
     * Devuelve un codigo unico usando el predicado recibido y, si hace falta,
     * el sufijo -2, -3, ... (limitado para evitar bucles).
     */
    public static String unique(String base, java.util.function.Predicate<String> exists) {
        String candidate = base;
        for (int suffix = 2; suffix < 1000; suffix++) {
            if (!exists.test(candidate)) {
                return candidate;
            }
            candidate = base + "-" + suffix;
        }
        throw new IllegalStateException("No se pudo generar un codigo unico para: " + base);
    }
}
