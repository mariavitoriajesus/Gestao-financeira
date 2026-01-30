package com.beca.financial.transaction_api.service;


import com.beca.financial.transaction_api.dto.ReportByTypeItem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportExportService {
    private final ReportService reportService;

    public ReportExportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public byte[] exportByTypeExcel(LocalDate from, LocalDate to) {
        List<ReportByTypeItem> rows = reportService.byType(from, to);

        try(XSSFWorkbook wb = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("by_type");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("type");
            header.createCell(1).setCellValue("count");
            header.createCell(2).setCellValue("totalAmount");

            int i = 1;
            for (ReportByTypeItem item : rows) {
                Row r = sheet.createRow(i++);
                r.createCell(0).setCellValue(item.type().name());
                r.createCell(1).setCellValue(item.count());
                r.createCell(2).setCellValue(item.totalAmount() != null ? item.totalAmount().doubleValue() : 0.0);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to generate Excel report", e);
        }
    }
}
