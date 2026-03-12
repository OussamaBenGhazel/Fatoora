package tn.tradenet.elfatoora.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.*;
import tn.tradenet.elfatoora.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/param/stock")
@RequiredArgsConstructor
public class StockParamController {

    private final DepartmentRepository departmentRepository;
    private final UnitOfMeasureRepository unitRepository;
    private final ArticleDepartmentRepository articleDepartmentRepository;
    private final ArticleSupplierRepository articleSupplierRepository;
    private final ArticleRepository articleRepository;
    private final SupplierRepository supplierRepository;

    // --- Departments ---

    @GetMapping("/departments")
    public List<Department> listDepartments() {
        return departmentRepository.findAll();
    }

    @PostMapping("/departments")
    public Department createDepartment(@RequestBody Department department) {
        department.setId(null);
        return departmentRepository.save(department);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return departmentRepository.findById(id)
            .map(existing -> {
                existing.setCode(department.getCode());
                existing.setName(department.getName());
                existing.setNature(department.getNature());
                return ResponseEntity.ok(departmentRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // In a real system we would check for existing article-department rows and block delete if any.
        departmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Units of measure ---

    @GetMapping("/units")
    public List<UnitOfMeasure> listUnits() {
        return unitRepository.findAll();
    }

    @PostMapping("/units")
    public UnitOfMeasure createUnit(@RequestBody UnitOfMeasure unit) {
        unit.setId(null);
        return unitRepository.save(unit);
    }

    @PutMapping("/units/{id}")
    public ResponseEntity<UnitOfMeasure> updateUnit(@PathVariable Long id, @RequestBody UnitOfMeasure unit) {
        return unitRepository.findById(id)
            .map(existing -> {
                existing.setCode(unit.getCode());
                existing.setName(unit.getName());
                existing.setAbbreviation(unit.getAbbreviation());
                return ResponseEntity.ok(unitRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/units/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        if (!unitRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        unitRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Article / Department assignments ---

    @GetMapping("/article-departments")
    public List<ArticleDepartment> listArticleDepartments(
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) Long articleId
    ) {
        if (departmentId != null) {
            return articleDepartmentRepository.findByDepartmentId(departmentId);
        }
        if (articleId != null) {
            return articleDepartmentRepository.findByArticleId(articleId);
        }
        return articleDepartmentRepository.findAll();
    }

    @PostMapping("/article-departments")
    public ResponseEntity<ArticleDepartment> createArticleDepartment(@RequestBody ArticleDepartment payload) {
        if (payload.getArticle() == null || payload.getArticle().getId() == null ||
            payload.getDepartment() == null || payload.getDepartment().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Article article = articleRepository.findById(payload.getArticle().getId()).orElseThrow();
        Department department = departmentRepository.findById(payload.getDepartment().getId()).orElseThrow();

        ArticleDepartment ad = ArticleDepartment.builder()
            .article(article)
            .department(department)
            .dosage(payload.getDosage())
            .salePrice(payload.getSalePrice())
            .stockMin(payload.getStockMin())
            .stockMax(payload.getStockMax())
            .build();

        return ResponseEntity.ok(articleDepartmentRepository.save(ad));
    }

    @PutMapping("/article-departments/{id}")
    public ResponseEntity<ArticleDepartment> updateArticleDepartment(@PathVariable Long id, @RequestBody ArticleDepartment payload) {
        return articleDepartmentRepository.findById(id)
            .map(existing -> {
                if (payload.getArticle() != null && payload.getArticle().getId() != null) {
                    Article article = articleRepository.findById(payload.getArticle().getId()).orElseThrow();
                    existing.setArticle(article);
                }
                if (payload.getDepartment() != null && payload.getDepartment().getId() != null) {
                    Department department = departmentRepository.findById(payload.getDepartment().getId()).orElseThrow();
                    existing.setDepartment(department);
                }
                existing.setDosage(payload.getDosage());
                existing.setSalePrice(payload.getSalePrice());
                existing.setStockMin(payload.getStockMin());
                existing.setStockMax(payload.getStockMax());
                return ResponseEntity.ok(articleDepartmentRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/article-departments/{id}")
    public ResponseEntity<Void> deleteArticleDepartment(@PathVariable Long id) {
        if (!articleDepartmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        articleDepartmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Article / Supplier assignments ---

    @GetMapping("/article-suppliers")
    public List<ArticleSupplier> listArticleSuppliers(
        @RequestParam(required = false) Long articleId,
        @RequestParam(required = false) Long supplierId
    ) {
        if (articleId != null) {
            return articleSupplierRepository.findByArticleId(articleId);
        }
        if (supplierId != null) {
            return articleSupplierRepository.findBySupplierId(supplierId);
        }
        return articleSupplierRepository.findAll();
    }

    @PostMapping("/article-suppliers")
    public ResponseEntity<ArticleSupplier> createArticleSupplier(@RequestBody ArticleSupplier payload) {
        if (payload.getArticle() == null || payload.getArticle().getId() == null ||
            payload.getSupplier() == null || payload.getSupplier().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Article article = articleRepository.findById(payload.getArticle().getId()).orElseThrow();
        Supplier supplier = supplierRepository.findById(payload.getSupplier().getId()).orElseThrow();

        ArticleSupplier as = ArticleSupplier.builder()
            .article(article)
            .supplier(supplier)
            .purchasePrice(payload.getPurchasePrice())
            .discountPercent(payload.getDiscountPercent())
            .conventionDate(payload.getConventionDate())
            .build();

        return ResponseEntity.ok(articleSupplierRepository.save(as));
    }

    @PutMapping("/article-suppliers/{id}")
    public ResponseEntity<ArticleSupplier> updateArticleSupplier(@PathVariable Long id, @RequestBody ArticleSupplier payload) {
        return articleSupplierRepository.findById(id)
            .map(existing -> {
                if (payload.getArticle() != null && payload.getArticle().getId() != null) {
                    Article article = articleRepository.findById(payload.getArticle().getId()).orElseThrow();
                    existing.setArticle(article);
                }
                if (payload.getSupplier() != null && payload.getSupplier().getId() != null) {
                    Supplier supplier = supplierRepository.findById(payload.getSupplier().getId()).orElseThrow();
                    existing.setSupplier(supplier);
                }
                existing.setPurchasePrice(payload.getPurchasePrice());
                existing.setDiscountPercent(payload.getDiscountPercent());
                existing.setConventionDate(payload.getConventionDate());
                return ResponseEntity.ok(articleSupplierRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/article-suppliers/{id}")
    public ResponseEntity<Void> deleteArticleSupplier(@PathVariable Long id) {
        if (!articleSupplierRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        articleSupplierRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Stock economat-style listing ---

    @GetMapping("/economat")
    public List<EconomatLine> listEconomat(
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) Long familyId,
        @RequestParam(required = false) Long subFamilyId
    ) {
        List<ArticleDepartment> ads = departmentId != null
            ? articleDepartmentRepository.findByDepartmentId(departmentId)
            : articleDepartmentRepository.findAll();

        return ads.stream()
            .filter(ad -> {
                Article article = ad.getArticle();
                if (article == null) return false;
                ArticleSubFamily sf = article.getSubFamily();
                if (sf == null) return familyId == null && subFamilyId == null;
                ArticleFamily fam = sf.getFamily();
                if (subFamilyId != null && (sf.getId() == null || !sf.getId().equals(subFamilyId))) {
                    return false;
                }
                if (familyId != null && (fam == null || fam.getId() == null || !fam.getId().equals(familyId))) {
                    return false;
                }
                return true;
            })
            .map(ad -> {
                Article article = ad.getArticle();
                ArticleSubFamily sf = article.getSubFamily();
                ArticleFamily fam = sf != null ? sf.getFamily() : null;
                Department dept = ad.getDepartment();
                EconomatLine line = new EconomatLine();
                line.setArticleId(article.getId());
                line.setArticleCode(article.getCode());
                line.setArticleName(article.getName());
                line.setDepartmentName(dept != null ? dept.getName() : null);
                line.setFamilyName(fam != null ? fam.getName() : null);
                line.setSubFamilyName(sf != null ? sf.getName() : null);
                line.setUnit(article.getUnit());
                line.setStockQuantity(article.getStockQuantity());
                line.setStockMin(ad.getStockMin());
                line.setStockMax(ad.getStockMax());
                line.setSalePrice(ad.getSalePrice());
                return line;
            })
            .collect(Collectors.toList());
    }

    @Data
    public static class EconomatLine {
        private Long articleId;
        private String articleCode;
        private String articleName;
        private String familyName;
        private String subFamilyName;
        private String departmentName;
        private String unit;
        private BigDecimal stockQuantity;
        private BigDecimal stockMin;
        private BigDecimal stockMax;
        private BigDecimal salePrice;
    }
}

