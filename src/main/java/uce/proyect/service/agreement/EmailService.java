package uce.proyect.service.agreement;

import freemarker.template.TemplateException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;

public interface EmailService {
    String enviarEmail();

    void enviarEmailPlan(String destinatario, LocalDate fechaInicio, LocalDate fechaFinal, String facultad, String fase) throws MessagingException, IOException, TemplateException;

    JSONObject enviarComprobante(JSONObject recursos) throws MessagingException, IOException, TemplateException;

    void enviarEmailCredenciales(String email, String nombreUsuario, String password) throws MessagingException, IOException, TemplateException;
}
