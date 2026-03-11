package tn.tradenet.elfatoora.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.ClientCategory;
import tn.tradenet.elfatoora.domain.Supplier;
import tn.tradenet.elfatoora.domain.SupplierCategory;
import tn.tradenet.elfatoora.repository.ClientCategoryRepository;
import tn.tradenet.elfatoora.repository.SupplierCategoryRepository;
import tn.tradenet.elfatoora.repository.SupplierRepository;

import java.util.List;

@RestController
@RequestMapping("/param")
@RequiredArgsConstructor
public class ClientSupplierParamController {

    private final ClientCategoryRepository clientCategoryRepository;
    private final SupplierCategoryRepository supplierCategoryRepository;
    private final SupplierRepository supplierRepository;

    // --- Client categories ---

    @GetMapping("/client-categories")
    public List<ClientCategory> listClientCategories() {
        return clientCategoryRepository.findAll();
    }

    @PostMapping("/client-categories")
    public ClientCategory createClientCategory(@RequestBody ClientCategory category) {
        category.setId(null);
        return clientCategoryRepository.save(category);
    }

    @PutMapping("/client-categories/{id}")
    public ResponseEntity<ClientCategory> updateClientCategory(@PathVariable Long id, @RequestBody ClientCategory category) {
        return clientCategoryRepository.findById(id)
            .map(existing -> {
                existing.setCode(category.getCode());
                existing.setName(category.getName());
                existing.setDescription(category.getDescription());
                return ResponseEntity.ok(clientCategoryRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/client-categories/{id}")
    public ResponseEntity<Void> deleteClientCategory(@PathVariable Long id) {
        if (!clientCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clientCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Supplier categories ---

    @GetMapping("/supplier-categories")
    public List<SupplierCategory> listSupplierCategories() {
        return supplierCategoryRepository.findAll();
    }

    @PostMapping("/supplier-categories")
    public SupplierCategory createSupplierCategory(@RequestBody SupplierCategory category) {
        category.setId(null);
        return supplierCategoryRepository.save(category);
    }

    @PutMapping("/supplier-categories/{id}")
    public ResponseEntity<SupplierCategory> updateSupplierCategory(@PathVariable Long id, @RequestBody SupplierCategory category) {
        return supplierCategoryRepository.findById(id)
            .map(existing -> {
                existing.setCode(category.getCode());
                existing.setName(category.getName());
                existing.setDescription(category.getDescription());
                return ResponseEntity.ok(supplierCategoryRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/supplier-categories/{id}")
    public ResponseEntity<Void> deleteSupplierCategory(@PathVariable Long id) {
        if (!supplierCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        supplierCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Suppliers ---

    @GetMapping("/suppliers")
    public List<Supplier> listSuppliers() {
        return supplierRepository.findAll();
    }

    @PostMapping("/suppliers")
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        supplier.setId(null);
        return supplierRepository.save(supplier);
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierRepository.findById(id)
            .map(existing -> {
                existing.setName(supplier.getName());
                existing.setMatriculeFiscale(supplier.getMatriculeFiscale());
                existing.setCategory(supplier.getCategory());
                existing.setEmail(supplier.getEmail());
                existing.setPhone(supplier.getPhone());
                existing.setAddress(supplier.getAddress());
                return ResponseEntity.ok(supplierRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        if (!supplierRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        supplierRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

