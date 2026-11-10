package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.response.ReportPreviewResponse;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.OrderDetail;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.repository.OrderDetailRepository;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/tools")
@RequiredArgsConstructor
public class AdminToolsController {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final DataSource dataSource;

    @GetMapping("/orders/preview")
    public ResponseEntity<ApiResponse<ReportPreviewResponse>> previewOrders(
            @RequestParam(defaultValue = "orders") String type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        List<Order> orders = filterOrders(type, status, from, to);
        List<ReportPreviewResponse.ProductRow> products = productRows(orders);
        BigDecimal total = orders.stream()
                .map(order -> order.getFinalAmount() == null ? BigDecimal.ZERO : order.getFinalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<ReportPreviewResponse.OrderRow> rows = orders.stream()
                .map(order -> ReportPreviewResponse.OrderRow.builder()
                        .orderCode(order.getOrderCode())
                        .customerName(order.getReceiverName())
                        .phone(order.getPhone())
                        .orderStatus(label(order.getOrderStatus()))
                        .paymentMethod(paymentLabel(order))
                        .paymentStatus(paymentState(order))
                        .totalAmount(order.getFinalAmount())
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();
        ReportPreviewResponse body = ReportPreviewResponse.builder()
                .type(type)
                .from(from)
                .to(to)
                .orderCount(orders.size())
                .totalAmount(total)
                .orders(rows)
                .products(products)
                .build();
        return ResponseEntity.ok(ApiResponse.success(body));
    }

    @GetMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(defaultValue = "orders") String type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) throws Exception {
        List<Order> orders = filterOrders(type, status, from, to);
        boolean products = "products".equalsIgnoreCase(type);
        if ("pdf".equalsIgnoreCase(format)) {
            byte[] pdf = products ? buildProductPdf(productRows(orders), from, to) : buildPdf(orders, type, from, to);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao-cao-aura.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }

        String csv = products ? productCsv(productRows(orders)) : orderCsv(orders);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao-cao-aura.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    private List<Order> filterOrders(String type, OrderStatus status, String from, String to) {
        LocalDateTime start = from == null || from.isBlank() ? LocalDate.now().minusYears(5).atStartOfDay() : LocalDate.parse(from).atStartOfDay();
        LocalDateTime end = to == null || to.isBlank() ? LocalDate.now().plusDays(1).atStartOfDay() : LocalDate.parse(to).plusDays(1).atStartOfDay();
        boolean revenue = "revenue".equalsIgnoreCase(type);
        return orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && !order.getCreatedAt().isBefore(start) && order.getCreatedAt().isBefore(end))
                .filter(order -> {
                    if (revenue) {
                        return order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.COMPLETED;
                    }
                    return status == null || order.getOrderStatus() == status;
                })
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private List<ReportPreviewResponse.ProductRow> productRows(List<Order> orders) {
        Map<String, BigDecimal[]> buckets = new LinkedHashMap<>();
        for (Order order : orders) {
            for (OrderDetail detail : orderDetailRepository.findByOrderId(order.getId())) {
                String name = detail.getProductName() == null ? "Sản phẩm" : detail.getProductName();
                BigDecimal[] bucket = buckets.computeIfAbsent(name, key -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                bucket[0] = bucket[0].add(BigDecimal.valueOf(detail.getQuantity() == null ? 0 : detail.getQuantity()));
                bucket[1] = bucket[1].add(detail.getSubtotal() == null ? BigDecimal.ZERO : detail.getSubtotal());
            }
        }
        List<ReportPreviewResponse.ProductRow> rows = new ArrayList<>();
        buckets.entrySet().stream()
                .sorted((left, right) -> right.getValue()[1].compareTo(left.getValue()[1]))
                .forEach(entry -> rows.add(ReportPreviewResponse.ProductRow.builder()
                        .productName(entry.getKey())
                        .quantity(entry.getValue()[0].intValue())
                        .revenue(entry.getValue()[1])
                        .build()));
        return rows;
    }

    private String orderCsv(List<Order> orders) {
        StringBuilder csv = new StringBuilder("\uFEFFMã đơn,Khách hàng,Điện thoại,Trạng thái,Thanh toán,Tổng tiền,Ngày tạo\n");
        for (Order order : orders) {
            csv.append(order.getOrderCode()).append(',')
                    .append(safe(order.getReceiverName())).append(',')
                    .append(safe(order.getPhone())).append(',')
                    .append(label(order.getOrderStatus())).append(',')
                    .append(paymentLabel(order)).append(' ').append(paymentState(order)).append(',')
                    .append(order.getFinalAmount()).append(',')
                    .append(order.getCreatedAt()).append('\n');
        }
        return csv.toString();
    }

    private String productCsv(List<ReportPreviewResponse.ProductRow> products) {
        StringBuilder csv = new StringBuilder("\uFEFFSản phẩm,Số lượng,Doanh thu\n");
        for (ReportPreviewResponse.ProductRow row : products) {
            csv.append(safe(row.getProductName())).append(',')
                    .append(row.getQuantity()).append(',')
                    .append(row.getRevenue()).append('\n');
        }
        return csv.toString();
    }

    private String safe(String value) {
        if (value == null) return "";
        return value.replace(",", " ");
    }

    @PostMapping("/restore-sample")
    public ResponseEntity<ApiResponse<Void>> restoreSample() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/sample/restore_catalog.sql"));
        }
        return ResponseEntity.ok(ApiResponse.empty("Đã khôi phục dữ liệu mẫu. Dữ liệu bán hàng hiện tại đã được xóa."));
    }

    private byte[] buildPdf(List<Order> orders, String type, String from, String to) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = vietnameseFont();
        String title = "revenue".equalsIgnoreCase(type) ? "Báo cáo doanh thu AURA" : "Báo cáo đơn hàng AURA";
        document.add(new Paragraph(title + rangeLabel(from, to), font));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        for (String header : List.of("Mã đơn", "Khách hàng", "Trạng thái", "Tổng tiền")) {
            table.addCell(new Paragraph(header, font));
        }
        for (Order order : orders) {
            table.addCell(new Paragraph(order.getOrderCode(), font));
            table.addCell(new Paragraph(order.getReceiverName() == null ? "" : order.getReceiverName(), font));
            table.addCell(new Paragraph(label(order.getOrderStatus()), font));
            table.addCell(new Paragraph(String.valueOf(order.getFinalAmount()), font));
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private byte[] buildProductPdf(List<ReportPreviewResponse.ProductRow> products, String from, String to) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = vietnameseFont();
        document.add(new Paragraph("Báo cáo sản phẩm AURA" + rangeLabel(from, to), font));
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        for (String header : List.of("Sản phẩm", "Số lượng", "Doanh thu")) {
            table.addCell(new Paragraph(header, font));
        }
        for (ReportPreviewResponse.ProductRow row : products) {
            table.addCell(new Paragraph(row.getProductName(), font));
            table.addCell(new Paragraph(String.valueOf(row.getQuantity()), font));
            table.addCell(new Paragraph(String.valueOf(row.getRevenue()), font));
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private String rangeLabel(String from, String to) {
        if ((from == null || from.isBlank()) && (to == null || to.isBlank())) return "";
        return " (" + (from == null ? "..." : from) + " - " + (to == null ? "..." : to) + ")";
    }

    private String paymentLabel(Order order) {
        String method = order.getPaymentMethod() == null ? "" : order.getPaymentMethod().name();
        return method.contains("VNPAY") ? "VNPay" : "COD";
    }

    private String paymentState(Order order) {
        if (order.getPaymentStatus() == null) return "Chưa thanh toán";
        return switch (order.getPaymentStatus()) {
            case COMPLETED -> "Đã thanh toán";
            case REFUNDED -> "Đã hoàn tiền";
            case FAILED -> "Thanh toán lỗi";
            default -> "Chưa thanh toán";
        };
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
