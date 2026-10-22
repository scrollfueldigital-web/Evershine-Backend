package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.CategoryVariant;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.CategoryVariantRepository;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.projectExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.projectExceptionHandler.ResourceNotFoundException;
import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.entity.Category;
import com.evershine.EvershineServer.productApi.entity.CategoryType;
import com.evershine.EvershineServer.productApi.repository.CategoryRepository;
import com.evershine.EvershineServer.productApi.repository.CategoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service @RequiredArgsConstructor
public class CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryVariantRepository categoryVariantRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true)
            }
    )
    public CatalogueResponseDto addCategoryType(UUID parentId,CatalogueRequestDto dto){

        Category category = categoryRepository.findById(parentId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("CategoryType does not exists with id: "+parentId)
                );

        if(categoryTypeRepository.existsByName(dto.getName()))
            throw new DuplicateFilterException("CategoryType name already exist with name: "+dto.getName());
        if(categoryTypeRepository.existsBySlug(dto.getSlug()))
            throw new DuplicateFilterException("CategoryType slug already exist with name: "+dto.getSlug());

        CategoryType type = toCategoryType(category,dto);
        type = categoryTypeRepository.save(type);
        return toCatalogueResponseDto(type);
    }

    @Transactional(readOnly = true)
    public List<CatalogueResponseDto> getAllCategoryTypes(){
        return categoryTypeRepository.findAll()
                .stream()
                .map(this::toCatalogueResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogueResponseDto getCategoryTypeById(UUID id){
        CategoryType type = categoryTypeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("CategoryType does not exist with id: "+id));
        return toCatalogueResponseDto(type);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true)
            }
    )
    public CatalogueResponseDto updateCategoryTypeById(UUID id, CatalogueRequestDto dto){
        CategoryType type = categoryTypeRepository.findById(id)
                .orElseThrow(
                        ()->new ResourceNotFoundException("CategoryType does not exists with id: "+id)
                );
        if(categoryTypeRepository.existsByName(dto.getName()))
            throw new DuplicateFilterException("CategoryType already exists with name: "+dto.getName());
        if(categoryTypeRepository.existsBySlug(dto.getSlug()))
            throw new DuplicateFilterException("CategoryType already exists with slug: "+dto.getSlug());

        type.setSlug(dto.getSlug());
        type.setName(dto.getName());
//        type.setCategory(category);  does not require this feature
        type = categoryTypeRepository.save(type);

        return toCatalogueResponseDto(type);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true),
                    @CacheEvict(value = "productList", allEntries = true)
            }
    )
    public String deleteCategoryType(UUID id){
        CategoryType type = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category Type does not exist with id: " + id));

        // 1. Get all child variants
        List<CategoryVariant> categoryVariants = categoryVariantRepository.findByCategoryType_Id(id);

        // 2. Collect ALL affected products without duplicates using a Set
        Set<Product> affectedProducts = new HashSet<>(productRepository.findByCategoryTypeId(id));
        for (CategoryVariant variant : categoryVariants) {
            affectedProducts.addAll(productRepository.findByCategoryVariantId(variant.getId()));
        }

        // 3. Detach type and variant from products (keep category!)
        for (Product p : affectedProducts) {
            p.setCategoryType(null);
            p.setCategoryVariant(null);
            p.setProductStatus(ProductStatus.UNAVAILABLE);
        }

        // 4. Flush changes to DB FIRST to clear all foreign keys
        productRepository.saveAllAndFlush(affectedProducts);

        // 5. Delete child variants in one batch, then the type
        if (!categoryVariants.isEmpty()) {
            categoryVariantRepository.deleteAll(categoryVariants);
        }
        categoryTypeRepository.deleteById(id);

        return "Category Type " + type.getName() + " deleted successfully! "
                + affectedProducts.size() + " products marked as unavailable.";
    }


    private CategoryType toCategoryType(Category category, CatalogueRequestDto dto) {
        CategoryType type = new CategoryType();
        type.setCategory(category);
        type.setName(dto.getName());
        type.setSlug(dto.getSlug());
        return type;
    }

    private CatalogueResponseDto toCatalogueResponseDto(CategoryType type) {
        return new CatalogueResponseDto(
                type.getId(),
                type.getName(),
                type.getSlug()
        );
    }
}
