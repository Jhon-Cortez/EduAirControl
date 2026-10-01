package com.eduaircontrol.backend.modules.classrooms.web;

import com.eduaircontrol.backend.modules.classrooms.application.EducationalEnvironmentService;
import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import com.eduaircontrol.backend.modules.classrooms.web.dto.EducationalEnvironmentCreateRequest;
import com.eduaircontrol.backend.modules.classrooms.web.dto.EducationalEnvironmentResponse;
import com.eduaircontrol.backend.modules.classrooms.web.dto.EducationalEnvironmentUpdateRequest;
import com.eduaircontrol.backend.modules.classrooms.web.dto.PageResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/educational-environments")
@RequiredArgsConstructor
public class EducationalEnvironmentController {

    private final EducationalEnvironmentService environmentService;

    @GetMapping
    public PageResponse<EducationalEnvironmentResponse> list(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "status", required = false) RecordStatus status,
            @RequestParam(name = "campusId", required = false) UUID campusId,
            @RequestParam(name = "environmentTypeId", required = false) UUID environmentTypeId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        PageParams.validate(page, limit);
        PageResult<EducationalEnvironment> result =
                environmentService.list(query, status, campusId, environmentTypeId, page, limit);
        return PageResponse.of(result, EducationalEnvironmentResponse::from);
    }

    @GetMapping("/{id}")
    public EducationalEnvironmentResponse get(@PathVariable UUID id) {
        return EducationalEnvironmentResponse.from(environmentService.get(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EducationalEnvironmentResponse> create(
            @Valid @RequestBody EducationalEnvironmentCreateRequest request) {
        EducationalEnvironment environment = environmentService.create(
                request.campusId(),
                request.code(),
                request.name(),
                request.environmentTypeId(),
                request.floor(),
                request.areaM2(),
                request.occupancyCapacity(),
                request.status());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(environment.getId())
                .toUri();
        return ResponseEntity.created(location).body(EducationalEnvironmentResponse.from(environment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public EducationalEnvironmentResponse update(
            @PathVariable UUID id, @Valid @RequestBody EducationalEnvironmentUpdateRequest request) {
        return EducationalEnvironmentResponse.from(environmentService.update(
                id,
                request.campusId(),
                request.code(),
                request.name(),
                request.environmentTypeId(),
                request.floor(),
                request.areaM2(),
                request.occupancyCapacity(),
                request.status()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        environmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
