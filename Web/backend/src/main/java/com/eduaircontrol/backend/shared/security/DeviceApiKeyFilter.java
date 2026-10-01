package com.eduaircontrol.backend.shared.security;

import com.eduaircontrol.backend.modules.sensors.application.DeviceAuthService;
import com.eduaircontrol.backend.modules.sensors.entity.Device;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Autentica la ingesta IoT por X-API-Key antes de llegar al handler, de modo
 * que una clave invalida devuelva 401 sin validar el cuerpo del payload.
 */
@Component
@RequiredArgsConstructor
public class DeviceApiKeyFilter extends OncePerRequestFilter {

    public static final String DEVICE_ROLE = "ROLE_DEVICE";

    private final DeviceAuthService deviceAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.startsWith("/ingest/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || apiKey.isBlank()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "X-API-Key requerida");
            return;
        }
        try {
            Device device = deviceAuthService.authenticate(apiKey);
            var authentication = new UsernamePasswordAuthenticationToken(
                    device, null, List.of(new SimpleGrantedAuthority(DEVICE_ROLE)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "API key inválida");
        }
    }

    private void writeError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }
}
