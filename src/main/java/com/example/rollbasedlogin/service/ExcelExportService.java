package com.example.rollbasedlogin.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.rollbasedlogin.model.Booking;

@Service
public class ExcelExportService {

    public ByteArrayInputStream bookingsToExcel(List<Booking> bookings) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Bookings");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Pickup");
            header.createCell(2).setCellValue("Drop");
            header.createCell(3).setCellValue("Booking Date");
            header.createCell(4).setCellValue("Status");

            int rowIdx = 1;
            for (Booking b : bookings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getId());
                row.createCell(1).setCellValue(b.getPickup());
                row.createCell(2).setCellValue(b.getDropLocation());
                row.createCell(3).setCellValue(b.getBookingDate().toString());
                row.createCell(4).setCellValue(b.getStatus());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
