package com.eduaircontrol.backend.modules.classrooms.web;

import com.eduaircontrol.backend.modules.classrooms.domain.exception.ValidationException;

final class PageParams {

    private PageParams() {
    }

    static void validate(int page, int limit) {
        if (page < 1) {
            throw new ValidationException("page must be 1 or greater");
        }
        if (limit < 1 || limit > 100) {
            throw new ValidationException("limit must be between 1 and 100");
        }
    }
}
