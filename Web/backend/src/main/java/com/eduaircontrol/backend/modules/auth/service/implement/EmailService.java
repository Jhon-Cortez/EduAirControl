package com.eduaircontrol.backend.modules.auth.service.implement;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@eduaircontrol.com}")
    private String from;

    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("EduAirControl - Código de recuperación de contraseña");
        message.setText("""
                Hola,

                recibes este correo porque se solicitó restablecer la contraseña de tu cuenta EduAirControl.

                Tu código de verificación es: %s

                Caduca en 15 minutos. Si no solicitaste este cambio, ignora este correo.

                Equipo EduAirControl
                """.formatted(code));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo de recuperación a {}: {}", to, e.getMessage());
            throw new IllegalStateException("No se pudo enviar el correo de recuperación");
        }
    }
}
