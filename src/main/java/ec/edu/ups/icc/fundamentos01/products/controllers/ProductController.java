package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import ec.edu.ups.icc.fundamentos01.shared.dto.PageableDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> findAll() {
        List<ProductResponseDto> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ProductResponseDto>> findAllPaginado(
            @Valid @ModelAttribute PageableDto pageable) {

        Page<ProductResponseDto> products = productService.findAllPaginado(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @Valid @ModelAttribute PageableDto pageable) {

        Slice<ProductResponseDto> products = productService.findAllSlice(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @Valid @ModelAttribute PageableDto pageable) {

        Page<ProductResponseDto> products = productService.findWithFilters(
                name, minPrice, maxPrice, categoryId, pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProductResponseDto>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @Valid @ModelAttribute PageableDto pageable) {

        Page<ProductResponseDto> products = productService.findByUserIdWithFilters(
                userId, name, minPrice, maxPrice, categoryId, pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> findAllSimple() {
        List<ProductResponseDto> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable("id") Long id) {
        ProductResponseDto product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/user/{userId}/simple")
    public ResponseEntity<List<ProductResponseDto>> findByUserIdSimple(@PathVariable("userId") Long userId) {
        List<ProductResponseDto> products = productService.findByUserId(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> findByCategoryId(@PathVariable("categoryId") Long categoryId) {
        List<ProductResponseDto> products = productService.findByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ProductResponseDto created = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ProductResponseDto updated = productService.update(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        productService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}