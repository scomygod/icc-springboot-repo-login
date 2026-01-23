package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.shared.dto.PageableDto;

public interface ProductService {

    ProductResponseDto create(CreateProductDto dto);

    List<ProductResponseDto> findAll();

    ProductResponseDto findById(Long id);

    List<ProductResponseDto> findByUserId(Long id);

    List<ProductResponseDto> findByCategoryId(Long id);

    ProductResponseDto update(Long id, UpdateProductDto dto);

    void delete(Long id);

    Page<ProductResponseDto> findAllPaginado(PageableDto pageableDto);

    Slice<ProductResponseDto> findAllSlice(PageableDto pageableDto);

    Page<ProductResponseDto> findWithFilters(
        String name,
        Double minPrice,
        Double maxPrice,
        Long categoryId,
        PageableDto pageableDto
    );

    Page<ProductResponseDto> findByUserIdWithFilters(
        Long userId,
        String name,
        Double minPrice,
        Double maxPrice,
        Long categoryId,
        PageableDto pageableDto
    );
}