package com.beca.financial.transaction_api.api;

import com.beca.financial.transaction_api.dto.ReportByTypeItem;
import com.beca.financial.transaction_api.dto.ReportSummaryResponse;
import com.beca.financial.transaction_api.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/by-type")
    public List<ReportByTypeItem> byType(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate from,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.byType(from, to);
    }

    public ReportSummaryResponse summary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
        return service.summary(from, to);
    }
}
