package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.projectExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.projectExceptionHandler.ResourceNotFoundException;
import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.entity.CategoryType;
import com.evershine.EvershineServer.productApi.entity.CategoryVariant;
import com.evershine.EvershineServer.productApi.repository.CategoryTypeRepository;
import com.evershine.EvershineServer.productApi.repository.CategoryVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryVariantService {

    private final CategoryVariantRepository categoryVariantRepository;
    private final CategoryTypeRepository categoryTypeRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true)
            }
    )
    public CatalogueResponseDto addCategoryVariant(
            UUID parentId,
            CatalogueRequestDto dto
    ) {

        // parentId = CategoryType ID
        CategoryType categoryType = categoryTypeRepository.findById(parentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "CategoryType does not exist with id: " + parentId
                        )
                );

        if (categoryVariantRepository.existsByName(dto.getName())) {
            throw new DuplicateFilterException(
                    "CategoryVariant name already exists: " + dto.getName()
            );
        }

        if (categoryVariantRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateFilterException(
                    "CategoryVariant slug already exists: " + dto.getSlug()
            );
        }

        CategoryVariant variant = toCategoryVariant(categoryType, dto);

        variant = categoryVariantRepository.save(variant);

        return toCatalogueResponseDto(variant);
    }


    @Transactional(readOnly = true)
    public List<CatalogueResponseDto> getAllCategoryVariants() {

        return categoryVariantRepository.findAll()
                .stream()
                .map(this::toCatalogueResponseDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public CatalogueResponseDto getCategoryVariantById(UUID id) {

        CategoryVariant variant = categoryVariantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "CategoryVariant does not exist with id: " + id
                        )
                );

        return toCatalogueResponseDto(variant);
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true)
            }
    )
    public CatalogueResponseDto updateCategoryVariantById(
            UUID id,
            CatalogueRequestDto dto
    ) {

        CategoryVariant variant = categoryVariantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "CategoryVariant does not exist with id: " + id
                        )
                );

        if (categoryVariantRepository.existsByName(dto.getName())) {
            throw new DuplicateFilterException(
                    "CategoryVariant already exists with name: " + dto.getName()
            );
        }

        if (categoryVariantRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateFilterException(
                    "CategoryVariant already exists with slug: " + dto.getSlug()
            );
        }

        variant.setName(dto.getName());
        variant.setSlug(dto.getSlug());

        // categoryType is intentionally not changed

        variant = categoryVariantRepository.save(variant);

        return toCatalogueResponseDto(variant);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true),
                    @CacheEvict(value = "productList", allEntries = true)
            }
    )
    public String deleteByCategoryVariantId(UUID id){
        categoryVariantRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category variant does not exist with id: "+id));

        // mark product unavailable and remove variant mark it as null instead of hard delete
//        long numberOfProducts = productRepository.deleteByCategoryVariant_Id(id);

        List<Product> productList = productRepository.findByCategoryVariantId(id);

        // remove variant, mark them as unavailable

        productList = productList.stream()
                        .map(p->{
                            p.setProductStatus(ProductStatus.UNAVAILABLE);
                            p.setCategoryVariant(null);
                            return p;
                        })
                                .toList();

        productRepository.saveAll(productList);
        categoryVariantRepository.deleteById(id);
        return "Category variants deleted successfully! "+productList.size()+" products are removed completely.";
    }

    private CategoryVariant toCategoryVariant(
            CategoryType categoryType,
            CatalogueRequestDto dto
    ) {

        CategoryVariant variant = new CategoryVariant();

        variant.setCategoryType(categoryType);
        variant.setName(dto.getName());
        variant.setSlug(dto.getSlug());

        return variant;
    }


    private CatalogueResponseDto toCatalogueResponseDto(
            CategoryVariant variant
    ) {

        return new CatalogueResponseDto(
                variant.getId(),
                variant.getName(),
                variant.getSlug()
        );
    }
}