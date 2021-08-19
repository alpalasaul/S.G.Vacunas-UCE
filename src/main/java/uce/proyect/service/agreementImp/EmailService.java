package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class EmailService {

    public static Integer NOTIFICACIONES_ENVIADAS = 0;

//    @Autowired
    private JavaMailSender javaMailSender;

//    @Value("${ruta.imagen}")
//    private String imagen;

    @Autowired
    ResourceLoader resourceLoader;

    public String enviarEmail() {
        var mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("erickdp@hotmail.com");
        mailMessage.setTo("sgvuce@gmail.com");
        mailMessage.setSubject("Vacunación");
        mailMessage.setText("Mensaje con tilde en Díaz");

        this.javaMailSender.send(mailMessage);

        return "Todo nice";
    }

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
    public void enviarComprobante() throws MessagingException, IOException {
        var mimeMessage = this.javaMailSender.createMimeMessage();
        var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        mimeMessageHelper.setFrom("sgvuce@gmail.com"); // cambiar destinatario a emisor, solo es para desarrollo
        mimeMessageHelper.setTo("sgvuce@gmail.com");
        mimeMessageHelper.setSubject("Calendario Vacunación");
        mimeMessageHelper.setText("<h4>MENSAJE ADJUNTO</h4>", true); // Se puede enviar html

        mimeMessageHelper.addAttachment("img.png", resourceLoader.getResource("classpath:static/img/logo_uce.png")); // Se envia la ruta definina dentro de resources statics img

        this.javaMailSender.send(mimeMessage);
    }


}
