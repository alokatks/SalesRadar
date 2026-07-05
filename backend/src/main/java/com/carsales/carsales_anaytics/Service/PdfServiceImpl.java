package com.carsales.carsales_anaytics.Service;

import com.carsales.carsales_anaytics.dto.MonthlyCountDto;
import com.carsales.carsales_anaytics.dto.YearlyCountDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {

    private final CarSalesService carSalesService;

    public PdfServiceImpl(CarSalesService carSalesService) {
        this.carSalesService = carSalesService;
    }

    @Override
    public byte[] generateSalesReport() {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // ── FONTS ──
            Font titleFont       = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD,   new BaseColor(52, 152, 219));
            Font headingFont     = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD,   new BaseColor(52, 152, 219));
            Font normalFont      = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,   BaseColor.WHITE);
            Font footerFont      = new Font(Font.FontFamily.HELVETICA,  9, Font.ITALIC, BaseColor.GRAY);

            // ── TITLE ──
            Paragraph title = new Paragraph("Car Sales Analytics Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // ── DATE ──
            String date = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            Paragraph dateP = new Paragraph("Generated on: " + date, normalFont);
            dateP.setAlignment(Element.ALIGN_CENTER);
            document.add(dateP);
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // ── YEARLY SECTION ──
            List<YearlyCountDto> yearlyData = carSalesService.getYearlyCarsCount();

            Paragraph yearlyHeading = new Paragraph("Yearly Sales Summary", headingFont);
            document.add(yearlyHeading);
            document.add(Chunk.NEWLINE);

            // ── METRICS ──
            long totalSales = yearlyData.stream().mapToLong(YearlyCountDto::count).sum();
            long maxSales   = yearlyData.stream().mapToLong(YearlyCountDto::count).max().orElse(0);
            int  bestYear   = yearlyData.stream()
                    .max((a, b) -> Long.compare(a.count(), b.count()))
                    .map(YearlyCountDto::year)
                    .orElse(0);

            // ── METRICS TABLE (3 columns) ──
            PdfPTable metricsTable = new PdfPTable(3);
            metricsTable.setWidthPercentage(100);
            metricsTable.setSpacingAfter(10);
            addMetricCell(metricsTable, "Total Sales",            String.valueOf(totalSales));
            addMetricCell(metricsTable, "Max Sales (Single Year)",String.valueOf(maxSales));
            addMetricCell(metricsTable, "Best Year",              String.valueOf(bestYear));
            document.add(metricsTable);
            document.add(Chunk.NEWLINE);

            // ── YEARLY DATA TABLE ──
            PdfPTable yearlyTable = new PdfPTable(2);
            yearlyTable.setWidthPercentage(50);
            yearlyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            yearlyTable.setSpacingAfter(10);
            addTableHeader(yearlyTable, "Year",         tableHeaderFont);
            addTableHeader(yearlyTable, "Total Sales",  tableHeaderFont);

            for (YearlyCountDto dto : yearlyData) {
                PdfPCell yearCell  = new PdfPCell(new Phrase(String.valueOf(dto.year()),  normalFont));
                PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(dto.count()), normalFont));
                yearCell.setPadding(6);
                countCell.setPadding(6);
                yearlyTable.addCell(yearCell);
                yearlyTable.addCell(countCell);
            }
            document.add(yearlyTable);
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // ── MONTHLY SECTION ──
            int currentYear = LocalDateTime.now().getYear();
            List<MonthlyCountDto> monthlyData = carSalesService.getMonthlyCountByYear(currentYear);

            Paragraph monthlyHeading = new Paragraph("Monthly Breakdown - " + currentYear, headingFont);
            document.add(monthlyHeading);
            document.add(Chunk.NEWLINE);

            String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            // ── MONTHLY DATA TABLE ──
            PdfPTable monthlyTable = new PdfPTable(2);
            monthlyTable.setWidthPercentage(50);
            monthlyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            monthlyTable.setSpacingAfter(10);
            addTableHeader(monthlyTable, "Month",       tableHeaderFont);
            addTableHeader(monthlyTable, "Sales Count", tableHeaderFont);

            for (MonthlyCountDto dto : monthlyData) {
                String monthName = (dto.month() >= 1 && dto.month() <= 12)
                        ? monthNames[dto.month()] : String.valueOf(dto.month());
                PdfPCell monthCell = new PdfPCell(new Phrase(monthName,                   normalFont));
                PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(dto.count()), normalFont));
                monthCell.setPadding(6);
                countCell.setPadding(6);
                monthlyTable.addCell(monthCell);
                monthlyTable.addCell(countCell);
            }
            document.add(monthlyTable);
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // ── FOOTER ──
            Paragraph footer = new Paragraph(
                    "Report generated by Car Sales Analytics Dashboard | By ALOK", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }

    // ── HELPER: Metric Cell ──
    private void addMetricCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new BaseColor(44, 62, 80));
        cell.setPadding(10);
        cell.addElement(new Paragraph(label,
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.LIGHT_GRAY)));
        cell.addElement(new Paragraph(value,
                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(52, 152, 219))));
        table.addCell(cell);
    }

    // ── HELPER: Table Header Cell ──
    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(52, 152, 219));
        cell.setPadding(8);
        table.addCell(cell);
    }
}