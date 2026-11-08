package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.repository.OrderRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tools")
@RequiredArgsConstructor
public class AdminToolsController {

    private final OrderRepository orderRepository;
    private final DataSource dataSource;

    @GetMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) throws Exception {
        LocalDateTime start = from == null || from.isBlank() ? LocalDate.now().minusYears(5).atStartOfDay() : LocalDate.parse(from).atStartOfDay();
        LocalDateTime end = to == null || to.isBlank() ? LocalDate.now().plusDays(1).atStartOfDay() : LocalDate.parse(to).plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && !order.getCreatedAt().isBefore(start) && order.getCreatedAt().isBefore(end))
                .filter(order -> status == null || order.getOrderStatus() == status)
                .toList();

        if ("pdf".equalsIgnoreCase(format)) {
            byte[] pdf = buildPdf(orders);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao-cao-don-hang.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }

        StringBuilder csv = new StringBuilder("\uFEFFMã đơn,Trạng thái,Thanh toán,Tổng tiền,Ngày tạo\n");
        for (Order order : orders) {
            csv.append(order.getOrderCode()).append(',')
                    .append(label(order.getOrderStatus())).append(',')
                    .append(order.getPaymentStatus()).append(',')
                    .append(order.getTotalAmount()).append(',')
                    .append(order.getCreatedAt()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao-cao-don-hang.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/restore-sample")
    public ResponseEntity<ApiResponse<Void>> restoreSample() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/sample/restore_catalog.sql"));
        }
        return ResponseEntity.ok(ApiResponse.empty("Đã khôi phục dữ liệu mẫu. Dữ liệu bán hàng hiện tại đã được xóa."));
    }

    private byte[] buildPdf(List<Order> orders) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = vietnameseFont();
        document.add(new Paragraph("Báo cáo đơn hàng AURA", font));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        for (String header : List.of("Mã đơn", "Trạng thái", "Tổng tiền", "Ngày tạo")) {
            table.addCell(new Paragraph(header, font));
        }
        for (Order order : orders) {
            table.addCell(new Paragraph(order.getOrderCode(), font));
            table.addCell(new Paragraph(label(order.getOrderStatus()), font));
            table.addCell(new Paragraph(String.valueOf(order.getTotalAmount()), font));
            table.addCell(new Paragraph(String.valueOf(order.getCreatedAt()), font));
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private Font vietnameseFont() {
        try {
            BaseFont base = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(base, 11);
        } catch (Exception ex) {
            return FontFactory.getFont(FontFactory.HELVETICA, 11);
        }
    }

    private String label(OrderStatus status) {
        if (status == null) return "";
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED, PROCESSING -> "Đã xác nhận";
            case SHIPPING -> "Đang giao";
            case DELIVERED, COMPLETED -> "Đã giao";
            case CANCELLED -> "Đã hủy";
            default -> status.name();
        };
    }
}
