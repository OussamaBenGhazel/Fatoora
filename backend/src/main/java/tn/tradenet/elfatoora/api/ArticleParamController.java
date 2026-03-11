package tn.tradenet.elfatoora.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.Article;
import tn.tradenet.elfatoora.domain.ArticleFamily;
import tn.tradenet.elfatoora.domain.ArticleSubFamily;
import tn.tradenet.elfatoora.repository.ArticleFamilyRepository;
import tn.tradenet.elfatoora.repository.ArticleRepository;
import tn.tradenet.elfatoora.repository.ArticleSubFamilyRepository;

import java.util.List;

@RestController
@RequestMapping("/param/articles")
@RequiredArgsConstructor
public class ArticleParamController {

    private final ArticleFamilyRepository familyRepository;
    private final ArticleSubFamilyRepository subFamilyRepository;
    private final ArticleRepository articleRepository;

    // --- Families ---

    @GetMapping("/families")
    public List<ArticleFamily> listFamilies() {
        return familyRepository.findAll();
    }

    @PostMapping("/families")
    public ArticleFamily createFamily(@RequestBody ArticleFamily family) {
        family.setId(null);
        return familyRepository.save(family);
    }

    @PutMapping("/families/{id}")
    public ResponseEntity<ArticleFamily> updateFamily(@PathVariable Long id, @RequestBody ArticleFamily family) {
        return familyRepository.findById(id)
            .map(existing -> {
                existing.setCode(family.getCode());
                existing.setName(family.getName());
                existing.setDescription(family.getDescription());
                return ResponseEntity.ok(familyRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/families/{id}")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id) {
        if (!familyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        familyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Sub-families ---

    @GetMapping("/sub-families")
    public List<ArticleSubFamily> listSubFamilies() {
        return subFamilyRepository.findAll();
    }

    @PostMapping("/sub-families")
    public ArticleSubFamily createSubFamily(@RequestBody ArticleSubFamily subFamily) {
        subFamily.setId(null);
        return subFamilyRepository.save(subFamily);
    }

    @PutMapping("/sub-families/{id}")
    public ResponseEntity<ArticleSubFamily> updateSubFamily(@PathVariable Long id, @RequestBody ArticleSubFamily subFamily) {
        return subFamilyRepository.findById(id)
            .map(existing -> {
                existing.setCode(subFamily.getCode());
                existing.setName(subFamily.getName());
                existing.setDescription(subFamily.getDescription());
                existing.setFamily(subFamily.getFamily());
                return ResponseEntity.ok(subFamilyRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/sub-families/{id}")
    public ResponseEntity<Void> deleteSubFamily(@PathVariable Long id) {
        if (!subFamilyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        subFamilyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Articles ---

    @GetMapping
    public List<Article> listArticles() {
        return articleRepository.findAll();
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        article.setId(null);
        return articleRepository.save(article);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return articleRepository.findById(id)
            .map(existing -> {
                existing.setCode(article.getCode());
                existing.setName(article.getName());
                existing.setUnit(article.getUnit());
                existing.setPurchasePrice(article.getPurchasePrice());
                existing.setSalePrice(article.getSalePrice());
                existing.setStockQuantity(article.getStockQuantity());
                existing.setSubFamily(article.getSubFamily());
                existing.setActive(article.isActive());
                return ResponseEntity.ok(articleRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        if (!articleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        articleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

