package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uce.proyect.service.agreement.EmailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class EmailServiceImp implements EmailService {

    public static Integer NOTIFICACIONES_ENVIADAS = 0;

    private JavaMailSender javaMailSender;

    private ResourceLoader resourceLoader;

    @Override
    public String enviarEmail() {
        var mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("sgvuce@gmail.com");
        mailMessage.setTo("sgvuce@gmail.com");
        mailMessage.setSubject("Vacunación");
        mailMessage.setText("Mensaje con tilde en Díaz");

        this.javaMailSender.send(mailMessage);

        return "Todo nice";
    }

    @Override
    public void enviarEmail(String destinatario, LocalDate fechaInicio, LocalDate fechaFinal, String facultad) {

        var mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(destinatario); // cambiar destinatario a emisor, solo es para desarrollo
        mailMessage.setTo("sgvuce@gmail.com");
        mailMessage.setSubject("Calendario Vacunación");
        mailMessage.setText(
                "Tenga un cordial saludo, le informamos el día de su vacunación"
                        .concat("\n\nFecha Inicio: ").concat(fechaInicio.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")))
                        .concat("\n\nFecha Final: ").concat(fechaFinal.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")))
                        .concat("\n\nLugar Vacunación: ".concat(facultad))
                        .concat("\n\n\nLos horarios de atención serán de 08:00 AM hasta 16:00 PM. Tome las devidas precauciones.")
                        .concat("\n\n\n\n\n\n")
                        .concat("No responder a este mensaje.")
                        .concat("\nPara mayor información visite www.prepago.com =)"));
        this.javaMailSender.send(mailMessage);

        NOTIFICACIONES_ENVIADAS++;
    }

    // Metodo que permite enviar archivo adjuntos en los email
    @Override
    public JSONObject enviarComprobante(JSONObject recursos) throws MessagingException, IOException {

        var carnet = (byte[]) recursos.get("recurso"); // tomo el pdf en bytes que genero el estudiante para enviarlo
        var mailDestinatario = recursos.get("mailDestinatario").toString(); // el destinatario igual

        var mimeMessage = this.javaMailSender.createMimeMessage();
        var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        mimeMessageHelper.setFrom("sgvuce@gmail.com"); // cambiar destinatario a emisor, solo es para desarrollo
        mimeMessageHelper.setTo(mailDestinatario);
        mimeMessageHelper.setSubject("Calendario Vacunación");
        mimeMessageHelper.setText("<h4>MENSAJE ADJUNTO</h4>", true); // Se puede enviar html

        mimeMessageHelper.addAttachment("carnetVacunacion.pdf", new ByteArrayResource(carnet)); // Se envia la ruta definina dentro de resources statics img

        this.javaMailSender.send(mimeMessage);

        var respuesta = new JSONObject();
        respuesta.put("destinatario", mailDestinatario);
        respuesta.put("estado", "enviado");

        respuesta.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")));

        return respuesta;
    }


}
