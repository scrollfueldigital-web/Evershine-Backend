package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.CategoryType;
import com.evershine.EvershineServer.productApi.entity.CategoryVariant;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.CategoryTypeRepository;
import com.evershine.EvershineServer.productApi.repository.CategoryVariantRepository;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.projectExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.projectExceptionHandler.ResourceNotFoundException;
import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.entity.Category;
import com.evershine.EvershineServer.productApi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service @RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryVariantRepository variantRepository;
    private final CategoryTypeRepository typeRepository;
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true)
            }
    )
    public CatalogueResponseDto addCategory(CatalogueRequestDto catalogue){

        if(categoryRepository.existsByName(catalogue.getName()))
            throw new DuplicateFilterException("Category name already exist with name: "+catalogue.getName());
        if(categoryRepository.existsBySlug(catalogue.getSlug()))
            throw new DuplicateFilterException("Category slug already exist with name: "+catalogue.getSlug());

        Category category = toCategory(catalogue);
        category = categoryRepository.save(category);
        return toCatalogueResponseDto(category);
    }

    @Transactional(readOnly = true)
    public List<CatalogueResponseDto> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(this::toCatalogueResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogueResponseDto getCategoryById(UUID id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category does not exist with id: "+id));
        return toCatalogueResponseDto(category);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true)
            }
    )
    public CatalogueResponseDto updateCategoryById(UUID id, CatalogueRequestDto categoryRequestDto){
        Category category = categoryRepository.findById(id)
                .orElseThrow(
                        ()->new ResourceNotFoundException("Category does not exists with id: "+id)
                );
        if(categoryRepository.existsByName(categoryRequestDto.getName()))
            throw new DuplicateFilterException("Category already exists with name: "+categoryRequestDto.getName());
        if(categoryRepository.existsBySlug(categoryRequestDto.getSlug()))
            throw new DuplicateFilterException("Category already exists with slug: "+categoryRequestDto.getSlug());

        category.setSlug(categoryRequestDto.getSlug());
        category.setName(categoryRequestDto.getName());
        category = categoryRepository.save(category);

        return toCatalogueResponseDto(category);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dashboard", allEntries = true),
                    @CacheEvict(value = "filters", allEntries = true),
                    @CacheEvict(value = "productList", allEntries = true)
            }
    )
    public String deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with id: " + id));

        // 1. Fetch entire hierarchy under this category
        List<CategoryType> types = typeRepository.findByCategory_Id(id);
        List<CategoryVariant> allVariants = new ArrayList<>();
        for (CategoryType t : types) {
            allVariants.addAll(variantRepository.findByCategoryType_Id(t.getId()));
        }

        // 2. Collect ALL unique products affected across Category, Types, and Variants
        Set<Product> affectedProducts = new HashSet<>(productRepository.findByCategoryId(id));
        for (CategoryType t : types) {
            affectedProducts.addAll(productRepository.findByCategoryTypeId(t.getId()));
        }
        for (CategoryVariant v : allVariants) {
            affectedProducts.addAll(productRepository.findByCategoryVariantId(v.getId()));
        }

        // 3. Detach all 3 from products
        for (Product p : affectedProducts) {
            p.setCategory(null);
            p.setCategoryType(null);
            p.setCategoryVariant(null);
            p.setProductStatus(ProductStatus.UNAVAILABLE);
        }

        // 4. Flush to database FIRST so foreign keys are cleared
        productRepository.saveAllAndFlush(affectedProducts);

        // 5. Delete children in batch, then the category
        if (!allVariants.isEmpty()) {
            variantRepository.deleteAll(allVariants);
        }
        if (!types.isEmpty()) {
            typeRepository.deleteAll(types);
        }
        categoryRepository.deleteById(id);

        return "Category " + category.getName() + " deleted successfully! "
                + affectedProducts.size() + " products marked as unavailable.";
    }


    private CatalogueResponseDto toCatalogueResponseDto(Category category) {
        return new CatalogueResponseDto(
                category.getId(),
                category.getName(),
                category.getSlug()
        );
    }

    private Category toCategory(CatalogueRequestDto catalogue) {
        Category category = new Category();
        category.setName(catalogue.getName());
        category.setSlug(catalogue.getSlug());
        return category;
    }
}
