package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.reporitory.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.models.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.repository.ProductRepository;
import ec.edu.ups.icc.fundamentos01.shared.dto.PageableDto;
import ec.edu.ups.icc.fundamentos01.users.models.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repository.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository productRepo,
            UserRepository userRepo,
            CategoryRepository categoryRepository) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepository;
        this.userRepo = userRepo;
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        UserEntity owner = userRepo.findById(dto.userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + dto.userId));

        Set<CategoryEntity> categories = validateAndGetCategories(dto.categoryIds);

        if (productRepo.findByName(dto.name).isPresent()) {
            throw new IllegalStateException("El nombre del producto ya está registrado");
        }

        Product product = Product.fromDto(dto);
        ProductEntity entity = product.toEntity(owner, categories);
        ProductEntity saved = productRepo.save(entity);

        return toResponseDto(saved);
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepo.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto findById(Long id) {
        return productRepo.findById(id)
                .map(this::toResponseDto)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));
    }

    @Override
    public List<ProductResponseDto> findByUserId(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado con ID: " + userId);
        }

        return productRepo.findByOwnerId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {
        if (!categoryRepo.existsById(categoryId)) {
            throw new NotFoundException("Categoría no encontrada con ID: " + categoryId);
        }

        return productRepo.findByCategoriesId(categoryId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {
        ProductEntity existing = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));

        Set<CategoryEntity> categories = validateAndGetCategories(dto.categoryIds);

        Product product = Product.fromEntity(existing);
        product.update(dto);

        ProductEntity updated = product.toEntity(existing.getOwner(), categories);
        updated.setId(id);

        ProductEntity saved = productRepo.save(updated);
        return toResponseDto(saved);
    }

    @Override
    public void delete(Long id) {
        ProductEntity product = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));

        productRepo.delete(product);
    }

    @Override
    public Page<ProductResponseDto> findAllPaginado(PageableDto pageableDto) {
        Pageable pageable = createPageable(pageableDto);
        Page<ProductEntity> productPage = productRepo.findAll(pageable);
        return productPage.map(this::toResponseDto);
    }

    @Override
    public Slice<ProductResponseDto> findAllSlice(PageableDto pageableDto) {
        Pageable pageable = createPageable(pageableDto);
        Slice<ProductEntity> productSlice = productRepo.findAllSlice(pageable); // ← AQUÍ: findAllSlice en lugar de
                                                                                // findAll
        return productSlice.map(this::toResponseDto);
    }

    @Override
    public Page<ProductResponseDto> findWithFilters(
            String name, Double minPrice, Double maxPrice, Long categoryId,
            PageableDto pageableDto) {

        validateFilterParameters(minPrice, maxPrice);
        Pageable pageable = createPageable(pageableDto);

        Page<ProductEntity> productPage = productRepo.findWithFilters(
                name, minPrice, maxPrice, categoryId, pageable);

        return productPage.map(this::toResponseDto);
    }

    @Override
    public Page<ProductResponseDto> findByUserIdWithFilters(
            Long userId, String name, Double minPrice, Double maxPrice, Long categoryId,
            PageableDto pageableDto) {

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado con ID: " + userId);
        }

        validateFilterParameters(minPrice, maxPrice);
        Pageable pageable = createPageable(pageableDto);

        Page<ProductEntity> productPage = productRepo.findByUserIdWithFilters(
                userId, name, minPrice, maxPrice, categoryId, pageable);

        return productPage.map(this::toResponseDto);
    }

    private Pageable createPageable(PageableDto pageableDto) {
        int page = pageableDto.getPage();
        int size = pageableDto.getSize();
        String[] sort = pageableDto.getSort();

        if (page < 0) {
            throw new BadRequestException("La página debe ser mayor o igual a 0");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("El tamaño debe estar entre 1 y 100");
        }

        Sort sortObj = createSort(sort);
        return PageRequest.of(page, size, sortObj);
    }

private Sort createSort(String[] sortParams) {
    if (sortParams == null || sortParams.length == 0) {
        return Sort.by("id");
    }

    List<Sort.Order> orders = new ArrayList<>();

    // Manejar dos casos:
    // 1. sort=price,desc (Spring lo recibe como ["price", "desc"])
    // 2. sort=price&sort=name,asc (Spring lo recibe como ["price", "name,asc"])
    
    for (int i = 0; i < sortParams.length; i++) {
        String param = sortParams[i].trim();
        String property;
        String direction = "asc"; // Default
        
        // Si contiene coma, es formato "property,direction"
        if (param.contains(",")) {
            String[] parts = param.split(",");
            property = parts[0].trim();
            direction = parts.length > 1 ? parts[1].trim() : "asc";
        } 
        // Si NO contiene coma, verificar si el siguiente elemento es "asc" o "desc"
        else {
            property = param;
            // Verificar si el siguiente parámetro es una dirección
            if (i + 1 < sortParams.length) {
                String nextParam = sortParams[i + 1].trim();
                if ("asc".equalsIgnoreCase(nextParam) || "desc".equalsIgnoreCase(nextParam)) {
                    direction = nextParam;
                    i++; // Saltar el siguiente elemento porque ya lo procesamos
                }
            }
        }

        // Validar solo la propiedad, NO la dirección
        if (!isValidSortProperty(property)) {
            throw new BadRequestException("Propiedad de ordenamiento no válida: " + property);
        }

        Sort.Order order = "desc".equalsIgnoreCase(direction)
                ? Sort.Order.desc(property)
                : Sort.Order.asc(property);

        orders.add(order);
    }

    return Sort.by(orders);
}

    private boolean isValidSortProperty(String property) {
        Set<String> allowedProperties = Set.of(
                "id", "name", "price", "createdAt", "updatedAt",
                "owner.name", "owner.email", "description");
        return allowedProperties.contains(property);
    }

    private void validateFilterParameters(Double minPrice, Double maxPrice) {
        if (minPrice != null && minPrice < 0) {
            throw new BadRequestException("El precio mínimo no puede ser negativo");
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new BadRequestException("El precio máximo no puede ser negativo");
        }

        if (minPrice != null && maxPrice != null && maxPrice < minPrice) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }
    }

    private ProductResponseDto toResponseDto(ProductEntity product) {
        ProductResponseDto dto = new ProductResponseDto();

        dto.id = product.getId();
        dto.name = product.getName();
        dto.price = product.getPrice();
        dto.description = product.getDescription();
        dto.createdAt = product.getCreatedAt();
        dto.updatedAt = product.getUpdatedAt();

        ProductResponseDto.UserSummaryDto userDto = new ProductResponseDto.UserSummaryDto();
        userDto.id = product.getOwner().getId();
        userDto.name = product.getOwner().getName();
        userDto.email = product.getOwner().getEmail();
        dto.user = userDto;

        List<CategoryResponseDto> categoryDtos = new ArrayList<>();
        for (CategoryEntity categoryEntity : product.getCategories()) {
            CategoryResponseDto categoryDto = new CategoryResponseDto();
            categoryDto.id = categoryEntity.getId();
            categoryDto.name = categoryEntity.getName();
            categoryDto.description = categoryEntity.getDescription();
            categoryDtos.add(categoryDto);
        }
        dto.categories = categoryDtos;

        return dto;
    }

    private Set<CategoryEntity> validateAndGetCategories(Set<Long> categoryIds) {
        Set<CategoryEntity> categories = new HashSet<>();

        for (Long categoryId : categoryIds) {
            CategoryEntity category = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + categoryId));
            categories.add(category);
        }

        return categories;
    }
}