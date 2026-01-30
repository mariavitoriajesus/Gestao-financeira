package com.beca.financial.transaction_api.api;

import com.beca.financial.transaction_api.service.ReportPdfService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportPdfController {
    private final ReportPdfService pdfService;

    public ReportPdfController(ReportPdfService pdfService) {
        this.pdfService = pdfService;
    }

    public ResponseEntity<byte[]> summaryPdf(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        byte[] pdf = pdfService.generateSummaryWithByType(from, to);

        String filename = "report_summary_" + from + "_to_" + to + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
