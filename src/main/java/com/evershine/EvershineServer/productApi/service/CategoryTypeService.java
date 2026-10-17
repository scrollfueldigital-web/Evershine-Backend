package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.entity.CategoryVariant;
import com.evershine.EvershineServer.productApi.repository.CategoryVariantRepository;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.projectExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.projectExceptionHandler.FilterOptionNotFoundException;
import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.entity.Category;
import com.evershine.EvershineServer.productApi.entity.CategoryType;
import com.evershine.EvershineServer.productApi.repository.CategoryRepository;
import com.evershine.EvershineServer.productApi.repository.CategoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service @RequiredArgsConstructor
public class CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryVariantRepository categoryVariantRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CatalogueResponseDto addCategoryType(UUID parentId,CatalogueRequestDto dto){

        Category category = categoryRepository.findById(parentId)
                .orElseThrow(
                        ()-> new FilterOptionNotFoundException("CategoryType does not exists with id: "+parentId)
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
                .orElseThrow(()->new FilterOptionNotFoundException("CategoryType does not exist with id: "+id));
        return toCatalogueResponseDto(type);
    }

    @Transactional
    public CatalogueResponseDto updateCategoryTypeById(UUID id, CatalogueRequestDto dto){
        CategoryType type = categoryTypeRepository.findById(id)
                .orElseThrow(
                        ()->new FilterOptionNotFoundException("CategoryType does not exists with id: "+id)
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
    public String deleteCategoryType(UUID id){
        //checking if category type exists?
        categoryTypeRepository.findById(id)
                .orElseThrow(()->new FilterOptionNotFoundException("Category Type does not exist with id: "+id));

        // deleting associated product first to avoid foreign key constraints
        AtomicLong numberOfProducts = new AtomicLong(productRepository.deleteByCategoryType_Id(id));

        // delete associated variants
        List<CategoryVariant> categoryVariants = categoryVariantRepository.findByCategoryType_Id(id);

        // delete each variant associated products
        categoryVariants.forEach(
                variant->{
                    numberOfProducts.addAndGet(productRepository.deleteByCategoryVariant_Id(variant.getId()));
                    categoryVariantRepository.deleteById(variant.getId());
                }
        );

        categoryTypeRepository.deleteById(id);
        return "Category Type deleted successfully! "+numberOfProducts+" products are removed completely";
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
