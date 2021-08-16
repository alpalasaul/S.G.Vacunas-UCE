package uce.proyect.service.view.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;
import uce.proyect.models.Carnet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component("carnet/pdf")
public class CarnetPdf extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter pdfWriter, HttpServletRequest request, HttpServletResponse response) throws Exception {
        var carnet = (Carnet) model.get("carnet");

//        var image = Image.getInstance("img/logo_uce.png");
//        image.setAbsolutePosition(171, 250);
        document.add(new Paragraph("sss"));

    }
}
