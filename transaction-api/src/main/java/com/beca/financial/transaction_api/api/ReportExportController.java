package com.beca.financial.transaction_api.api;


import com.beca.financial.transaction_api.service.ReportExportService;
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
public class ReportExportController {
    private final ReportExportService exportService;

    public ReportExportController(ReportExportService exportService) {
        this.exportService = exportService;
    }

    public ResponseEntity<byte[]> exportByTypeExcel(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate from,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate to) {
        byte[] file = exportService.exportByTypeExcel(from, to);
        String filename = "report_by_type_" + from + "_to_" + to + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return ResponseEntity.ok().headers(headers).body(file);
    }
}
