package com.eduaircontrol.backend.modules.analysis.infrastructure.web;

import com.eduaircontrol.backend.modules.analysis.application.FindAnalysisService;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatusCode;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import com.eduaircontrol.backend.modules.analysis.domain.port.in.FindAnalysisUseCase;
import com.eduaircontrol.backend.modules.analysis.domain.port.in.RequestAnalysisUseCase;
import com.eduaircontrol.backend.modules.analysis.infrastructure.web.dto.AnalysisRequest;
import com.eduaircontrol.backend.modules.analysis.infrastructure.web.dto.AnalysisResponse;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * API del analisis historico (contrato: 07-api/contracts/openapi/ms-monitoreo.yaml).
 *
 * <p>Cumple HU-ANA-001 a HU-ANA-004: analizar por dia, semana, mes y ano.
 */
@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private static final int MAX_LIMIT = 100;

    private final RequestAnalysisUseCase requestAnalysisUseCase;
    private final FindAnalysisUseCase findAnalysisUseCase;
    private final FindAnalysisService findAnalysisService;
    private final VariableCatalogPort variableCatalogPort;
    private final UserIdentityPort userIdentityPort;

    @PostMapping
    public ResponseEntity<AnalysisResponse> create(@Valid @RequestBody AnalysisRequest request,
                                                   Authentication authentication) {
        UUID requestedBy = userIdentityPort.idByEmail(authentication.getName()).orElse(null);
        EnvironmentalAnalysis analysis = requestAnalysisUseCase.execute(new RequestAnalysisUseCase.Command(
                request.environmentId(),
                request.period(),
                request.referenceInstant(),
                requestedBy));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(analysis.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(analysis, request.period()));
    }

    @GetMapping
    public AnalysisPageResponse list(
            @RequestParam(name = "environmentId", required = false) UUID environmentId,
            @RequestParam(name = "status", required = false) AnalysisStatusCode status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1,
                Math.min(Math.max(limit, 1), MAX_LIMIT),
                Sort.by(Sort.Direction.DESC, "periodStart"));
        Page<EnvironmentalAnalysis> result = findAnalysisUseCase.search(
                new FindAnalysisUseCase.Query(environmentId, status, pageable));

        return new AnalysisPageResponse(
                result.getContent().stream().map(analysis -> toResponse(analysis, null)).toList(),
                new PageMeta(result.getNumber() + 1, result.getSize(),
                        result.getTotalElements(), result.getTotalPages()));
    }

    @GetMapping("/{id}")
    public AnalysisResponse get(@PathVariable UUID id) {
        return toResponse(findAnalysisService.require(id), null);
    }

    @GetMapping("/latest")
    public ResponseEntity<AnalysisResponse> latest(
            @RequestParam(name = "environmentId") UUID environmentId,
            @RequestParam(name = "period", defaultValue = "DAY") AnalysisPeriod period,
            @RequestParam(name = "referenceDate", required = false) LocalDate referenceDate) {
        return findAnalysisUseCase
                .latestCompleted(environmentId, period,
                        referenceDate == null ? null
                                : referenceDate.atStartOfDay(ZoneOffset.UTC).toInstant())
                .map(analysis -> ResponseEntity.ok(toResponse(analysis, period)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private AnalysisResponse toResponse(EnvironmentalAnalysis analysis, AnalysisPeriod period) {
        Map<UUID, String> variableCodes = AnalysisResponse.variableCodeIndex(variableCatalogPort.findAll());
        return AnalysisResponse.of(analysis, period, variableCodes);
    }

    public record AnalysisPageResponse(List<AnalysisResponse> items, PageMeta meta) {
    }

    public record PageMeta(int page, int limit, long total, int totalPages) {
    }
}