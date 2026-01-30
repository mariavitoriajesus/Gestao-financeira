package com.beca.financial.transaction_api.service;

import com.beca.financial.transaction_api.dto.ReportByTypeItem;
import com.beca.financial.transaction_api.dto.ReportSummaryResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportPdfService {

    private final ReportService reportService;

    public ReportPdfService(ReportService reportService) {
        this.reportService = reportService;
    }

    public byte[] generateSummaryWithByType(LocalDate from, LocalDate to) {
        ReportSummaryResponse summary = reportService.summary(from, to);
        List<ReportByTypeItem> items = reportService.byType(from, to);

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = 780;
                float leading = 16;

                y = writeLine(cs, PDType1Font.HELVETICA_BOLD, 16, margin, y, "Transaction Report (Summary + By Type)");

                y -= 6;
                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "Period: " + from + " to " + to);

                y -= 10;

                y = writeLine(cs, PDType1Font.HELVETICA_BOLD, 12, margin, y, "Summary");
                y -= 4;

                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "Total Count: " + summary.totalCount());

                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "Total Amount: " + money(summary.totalAmount()));

                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "ENTRADA: " + money(summary.totalEntrada()));

                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "SAIDA: " + money(summary.totalSaida()));

                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "TRANSFERENCIA: " + money(summary.totalTransferencia()));

                y = writeLine(cs, PDType1Font.HELVETICA, 11, margin, y, "CAMBIO: " + money(summary.totalCambio()));

                y -= 14;

                y = writeLine(cs, PDType1Font.HELVETICA_BOLD, 12, margin, y, "By Type");
                y -= 8;

                float col1 = margin;
                float col2 = margin + 220;
                float col3 = margin + 320;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                cs.beginText(); cs.newLineAtOffset(col1, y); cs.showText("TYPE"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col2, y); cs.showText("COUNT"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col3, y); cs.showText("TOTAL"); cs.endText();

                y -= leading;

                cs.setFont(PDType1Font.HELVETICA, 11);

                // Rows
                for (ReportByTypeItem item : items) {
                    if (y < 80) {
                        cs.close();

                        PDPage newPage = new PDPage(PDRectangle.A4);
                        doc.addPage(newPage);
                        y = 780;

                        break;
                    }

                    cs.beginText(); cs.newLineAtOffset(col1, y); cs.showText(item.type().name()); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(col2, y); cs.showText(String.valueOf(item.count())); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(col3, y); cs.showText(money(item.totalAmount())); cs.endText();

                    y -= leading;
                }
            }

            doc.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF report", e);
        }
    }

    private static float writeLine(PDPageContentStream cs, PDType1Font font, int size,
                                   float x, float y, String text) throws IOException {
        cs.setFont(font, size);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - 16;
    }

    private static String money(BigDecimal v) {
        if (v == null) return "0.00";
        return v.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
