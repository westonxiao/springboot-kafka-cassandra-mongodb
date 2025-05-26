package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// In OrderReportService.java
@Service
public class OrderReportGenerator {
    private final ExecutorService reportExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void generateBulkReports(List<Order> orders) {
        orders.forEach(order ->
                reportExecutor.submit(() -> { //async processing
                    // CPU-intensive PDF/Excel generation
                    generateReportSync(order); // Synchronous operation
                    //generateReportAsync(order); // asynchronous operation
                    //if require_new, new thread out of upper level thread pool
                })
        );
    }

    private static final Logger logger = LoggerFactory.getLogger(OrderReportGenerator.class);

    /**
     * Synchronous report generation for a single order
     * @param order The order to generate report for
     * @return Path to the generated report file
     * @throws ReportGenerationException If report generation fails
     */
    public Path generateReportSync(Order order) throws ReportGenerationException {
        logger.info("Starting report generation for order {}", order.getId());

        // 1. Validate input
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        // 2. Prepare data (CPU-intensive)
        ReportData data = prepareReportData(order);

        // 3. Generate document (CPU-intensive)
        byte[] pdfContent = generatePdfContent(data);

        // 4. Save to filesystem
        Path reportPath = saveToFileSystem(order.getId(), pdfContent);

        logger.info("Completed report for order {}", order.getId());
        return reportPath;
    }

    //async
    public CompletableFuture<Path> generateReportAsync(Order order) {
        return CompletableFuture.supplyAsync(() -> generateReportSync(order),
                reportExecutor);
        //or runAsync
    }
    private ReportData prepareReportData(Order order) {
        // CPU-intensive data aggregation
        ReportData data = new ReportData();
        /*data.setOrderDetails(order);
        data.setCalculations(performComplexCalculations(order));
        data.setFormattedItems(formatOrderItems(order.getItems()));*/
        return data;
    }

    private byte[] generatePdfContent(ReportData data) {
        // Example using Apache PDFBox (CPU-heavy)
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // PDF generation logic
                //contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                //contentStream.showText("Order Report: " + data.getOrderDetails().getId());
                contentStream.endText();
                // ... more content
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ReportGenerationException("PDF generation failed", e);
        }
    }

    private Path saveToFileSystem(Long orderId, byte[] content) {
        Path path = Paths.get("/reports", "order_" + orderId + ".pdf");
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, content, StandardOpenOption.CREATE);
            return path;
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to save report", e);
        }
    }

    // Supporting classes
    private static class ReportData {
        private Order orderDetails;
        private Map<String, BigDecimal> calculations;
        private List<String> formattedItems;
        // getters/setters
    }

    public static class ReportGenerationException extends RuntimeException {
        public ReportGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}