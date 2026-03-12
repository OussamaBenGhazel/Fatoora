package tn.tradenet.elfatoora.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.*;
import tn.tradenet.elfatoora.repository.GoodsReceiptRepository;
import tn.tradenet.elfatoora.repository.PurchaseOrderRepository;
import tn.tradenet.elfatoora.repository.PurchaseRequestRepository;
import tn.tradenet.elfatoora.service.PurchaseService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;

    // --- Requests ---

    @GetMapping("/requests")
    public List<PurchaseRequest> listRequests() {
        return purchaseRequestRepository.findAll();
    }

    @PostMapping("/requests")
    public ResponseEntity<PurchaseRequest> createRequest(@RequestBody CreateRequestDto dto, @AuthenticationPrincipal UserDetails user) {
        String username = user != null ? user.getUsername() : "system";
        LocalDate needed = dto.getNeededDate();
        PurchaseRequest req = purchaseService.createRequest(dto.getDepartmentId(), username, needed, dto.toLines());
        return ResponseEntity.ok(req);
    }

    @PostMapping("/requests/{id}/status")
    public ResponseEntity<PurchaseRequest> updateRequestStatus(@PathVariable Long id, @RequestParam PurchaseRequest.Status status) {
        return ResponseEntity.ok(purchaseService.updateRequestStatus(id, status));
    }

    // --- Orders ---

    @GetMapping("/orders")
    public List<PurchaseOrder> listOrders() {
        return purchaseOrderRepository.findAll();
    }

    @PostMapping("/orders")
    public ResponseEntity<PurchaseOrder> createOrder(@RequestBody CreateOrderDto dto, @AuthenticationPrincipal UserDetails user) {
        String username = user != null ? user.getUsername() : "system";
        PurchaseOrder order = purchaseService.createOrder(
            dto.getSupplierId(),
            dto.getDepartmentId(),
            dto.getRequestId(),
            username,
            dto.getOrderDate(),
            dto.toLines()
        );
        return ResponseEntity.ok(order);
    }

    @PostMapping("/orders/{id}/status")
    public ResponseEntity<PurchaseOrder> updateOrderStatus(@PathVariable Long id, @RequestParam PurchaseOrder.Status status) {
        return ResponseEntity.ok(purchaseService.updateOrderStatus(id, status));
    }

    // --- Goods receipts ---

    @GetMapping("/receipts")
    public List<GoodsReceipt> listReceipts() {
        return goodsReceiptRepository.findAll();
    }

    @PostMapping("/receipts")
    public ResponseEntity<GoodsReceipt> createReceipt(@RequestBody CreateReceiptDto dto, @AuthenticationPrincipal UserDetails user) {
        String username = user != null ? user.getUsername() : "system";
        GoodsReceipt receipt = purchaseService.createReceipt(
            dto.getOrderId(),
            username,
            dto.getReceiptDate(),
            dto.toLines()
        );
        return ResponseEntity.ok(receipt);
    }

    // --- DTOs ---

    @Data
    public static class CreateRequestDto {
        private Long departmentId;
        private LocalDate neededDate;
        private List<Line> lines;

        public List<PurchaseRequestLine> toLines() {
            return lines.stream().map(l -> PurchaseRequestLine.builder()
                .article(Article.builder().id(l.getArticleId()).build())
                .quantity(l.getQuantity())
                .comment(l.getComment())
                .build()).toList();
        }

        @Data
        public static class Line {
            private Long articleId;
            private java.math.BigDecimal quantity;
            private String comment;
        }
    }

    @Data
    public static class CreateOrderDto {
        private Long supplierId;
        private Long departmentId;
        private Long requestId;
        private LocalDate orderDate;
        private List<Line> lines;

        public List<PurchaseOrderLine> toLines() {
            return lines.stream().map(l -> PurchaseOrderLine.builder()
                .article(Article.builder().id(l.getArticleId()).build())
                .quantity(l.getQuantity())
                .unitPrice(l.getUnitPrice())
                .discountPercent(l.getDiscountPercent())
                .build()).toList();
        }

        @Data
        public static class Line {
            private Long articleId;
            private java.math.BigDecimal quantity;
            private java.math.BigDecimal unitPrice;
            private java.math.BigDecimal discountPercent;
        }
    }

    @Data
    public static class CreateReceiptDto {
        private Long orderId;
        private LocalDate receiptDate;
        private List<Line> lines;

        public List<GoodsReceiptLine> toLines() {
            return lines.stream().map(l -> GoodsReceiptLine.builder()
                .article(Article.builder().id(l.getArticleId()).build())
                .quantity(l.getQuantity())
                .unitPrice(l.getUnitPrice())
                .build()).toList();
        }

        @Data
        public static class Line {
            private Long articleId;
            private java.math.BigDecimal quantity;
            private java.math.BigDecimal unitPrice;
        }
    }
}

