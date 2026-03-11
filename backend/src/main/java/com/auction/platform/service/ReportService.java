package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class ReportService {

    public void exportLotReport(HttpServletResponse response, Lot lot) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph title = new Paragraph("Auction Results: " + lot.getTitle(), fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        Paragraph details = new Paragraph("\n" +
                "Lot ID: " + lot.getId() + "\n" +
                "Final Price: $" + lot.getCurrentPrice() + "\n" +
                "Status: " + lot.getStatus() + "\n" +
                "Closed at: " + lot.getEndTime() + "\n"
        );
        document.add(details);

        document.add(new Paragraph("\nThis is an official transaction record."));
        document.close();
    }
}