package tn.tradenet.elfatoora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.tradenet.elfatoora.domain.*;
import tn.tradenet.elfatoora.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final DepartmentRepository departmentRepository;
    private final ArticleRepository articleRepository;
    private final SupplierRepository supplierRepository;

    // --- Requests ---

    @Transactional
    public PurchaseRequest createRequest(Long departmentId, String requestedBy, LocalDate neededDate, List<PurchaseRequestLine> lines) {
        Department dept = departmentRepository.findById(departmentId).orElseThrow();
        PurchaseRequest req = PurchaseRequest.builder()
            .department(dept)
            .requestedBy(requestedBy)
            .neededDate(neededDate)
            .status(PurchaseRequest.Status.DRAFT)
            .build();
        for (PurchaseRequestLine line : lines) {
            Article art = articleRepository.findById(line.getArticle().getId()).orElseThrow();
            PurchaseRequestLine l = PurchaseRequestLine.builder()
                .request(req)
                .article(art)
                .quantity(line.getQuantity())
                .comment(line.getComment())
                .build();
            req.getLines().add(l);
        }
        return purchaseRequestRepository.save(req);
    }

    @Transactional
    public PurchaseRequest updateRequestStatus(Long id, PurchaseRequest.Status status) {
        PurchaseRequest req = purchaseRequestRepository.findById(id).orElseThrow();
        req.setStatus(status);
        return purchaseRequestRepository.save(req);
    }

    // --- Orders ---

    @Transactional
    public PurchaseOrder createOrder(Long supplierId, Long departmentId, Long requestId, String createdBy, LocalDate orderDate, List<PurchaseOrderLine> lines) {
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow();
        Department dept = departmentId != null ? departmentRepository.findById(departmentId).orElse(null) : null;
        PurchaseRequest origin = requestId != null ? purchaseRequestRepository.findById(requestId).orElse(null) : null;

        PurchaseOrder order = PurchaseOrder.builder()
            .supplier(supplier)
            .department(dept)
            .originRequest(origin)
            .createdBy(createdBy)
            .orderDate(orderDate)
            .status(PurchaseOrder.Status.DRAFT)
            .build();

        for (PurchaseOrderLine line : lines) {
            Article art = articleRepository.findById(line.getArticle().getId()).orElseThrow();
            PurchaseOrderLine l = PurchaseOrderLine.builder()
                .order(order)
                .article(art)
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .discountPercent(line.getDiscountPercent())
                .build();
            order.getLines().add(l);
        }

        order = purchaseOrderRepository.save(order);
        if (origin != null && origin.getStatus() == PurchaseRequest.Status.SUBMITTED) {
            origin.setStatus(PurchaseRequest.Status.APPROVED);
            purchaseRequestRepository.save(origin);
        }
        return order;
    }

    @Transactional
    public PurchaseOrder updateOrderStatus(Long id, PurchaseOrder.Status status) {
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return purchaseOrderRepository.save(order);
    }

    // --- Goods receipts ---

    @Transactional
    public GoodsReceipt createReceipt(Long orderId, String receivedBy, LocalDate receiptDate, List<GoodsReceiptLine> lines) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId).orElseThrow();
        GoodsReceipt receipt = GoodsReceipt.builder()
            .order(order)
            .receivedBy(receivedBy)
            .receiptDate(receiptDate)
            .status(GoodsReceipt.Status.PENDING)
            .build();

        for (GoodsReceiptLine line : lines) {
            Article art = articleRepository.findById(line.getArticle().getId()).orElseThrow();
            GoodsReceiptLine l = GoodsReceiptLine.builder()
                .receipt(receipt)
                .article(art)
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .build();
            receipt.getLines().add(l);

            // Update article stock quantity (simple global stock for now).
            BigDecimal current = art.getStockQuantity() != null ? art.getStockQuantity() : BigDecimal.ZERO;
            art.setStockQuantity(current.add(line.getQuantity() != null ? line.getQuantity() : BigDecimal.ZERO));
            articleRepository.save(art);
        }

        receipt.setStatus(GoodsReceipt.Status.VALIDATED);
        order.setStatus(PurchaseOrder.Status.VALIDATED);
        purchaseOrderRepository.save(order);
        return goodsReceiptRepository.save(receipt);
    }
}

