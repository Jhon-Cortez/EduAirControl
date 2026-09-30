package com.eduaircontrol.backend.modules.device.service.implement;

import com.eduaircontrol.backend.modules.device.dto.request.DeviceRequest;
import com.eduaircontrol.backend.modules.device.dto.response.DeviceResponse;
import com.eduaircontrol.backend.modules.device.entity.Device;
import com.eduaircontrol.backend.modules.device.repository.DeviceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceResponse crear(DeviceRequest request) {
        String mac = normalizarMac(request.getMacAddress());
        if (deviceRepository.existsByMacAddress(mac)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un dispositivo con la MAC: " + mac);
        }

        Device device = Device.builder()
                .macAddress(mac)
                .nombre(request.getNombre())
                .tipo(request.getTipo() != null ? request.getTipo() : "esp32")
                .idAula(request.getIdAula())
                .ssid(request.getSsid())
                .estado(request.getEstado() != null ? request.getEstado() : "pendiente")
                .firmwareVersion(request.getFirmwareVersion())
                .build();

        return toResponse(deviceRepository.save(device));
    }

    public List<DeviceResponse> listarTodos() {
        return deviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DeviceResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    public DeviceResponse actualizar(Long id, DeviceRequest request) {
        Device device = buscar(id);
        String mac = normalizarMac(request.getMacAddress());

        if (!device.getMacAddress().equals(mac) && deviceRepository.existsByMacAddress(mac)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un dispositivo con la MAC: " + mac);
        }

        device.setMacAddress(mac);
        device.setNombre(request.getNombre());
        if (request.getTipo() != null) {
            device.setTipo(request.getTipo());
        }
        device.setIdAula(request.getIdAula());
        device.setSsid(request.getSsid());
        if (request.getEstado() != null) {
            device.setEstado(request.getEstado());
        }
        device.setFirmwareVersion(request.getFirmwareVersion());

        return toResponse(deviceRepository.save(device));
    }

    public void eliminar(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Dispositivo no encontrado con id: " + id);
        }
        deviceRepository.deleteById(id);
    }

    private Device buscar(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Dispositivo no encontrado con id: " + id));
    }

    private String normalizarMac(String macAddress) {
        return macAddress == null ? null : macAddress.trim().toUpperCase();
    }

    private DeviceResponse toResponse(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .macAddress(device.getMacAddress())
                .nombre(device.getNombre())
                .tipo(device.getTipo())
                .idAula(device.getIdAula())
                .ssid(device.getSsid())
                .estado(device.getEstado())
                .firmwareVersion(device.getFirmwareVersion())
                .fechaCreacion(device.getFechaCreacion())
                .fechaActualizacion(device.getFechaActualizacion())
                .build();
    }
}
