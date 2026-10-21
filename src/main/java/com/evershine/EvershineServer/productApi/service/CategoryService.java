package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.entity.CategoryType;
import com.evershine.EvershineServer.productApi.entity.CategoryVariant;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryVariantRepository variantRepository;
    private final CategoryTypeRepository typeRepository;
    @Transactional
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
    public String deleteCategory(UUID id) {

        categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category does not exist with id: " + id));

        long numberOfProducts = productRepository.deleteByCategory_Id(id);

        List<CategoryType> types = typeRepository.findByCategory_Id(id);

        for (CategoryType categoryType : types) {

            // Delete products directly associated with this type
            numberOfProducts += productRepository
                    .deleteByCategoryType_Id(categoryType.getId());

            // Fetch variants using the TYPE ID
            List<CategoryVariant> variants =
                    variantRepository.findByCategoryType_Id(categoryType.getId());

            for (CategoryVariant variant : variants) {

                numberOfProducts += productRepository
                        .deleteByCategoryVariant_Id(variant.getId());

                variantRepository.deleteById(variant.getId());
            }

            typeRepository.deleteById(categoryType.getId());
        }

        categoryRepository.deleteById(id);

        return "Category deleted successfully! "
                + numberOfProducts + " products removed completely";
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
