package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.projectExceptionHandler.ResourceNotFoundException;
import com.evershine.EvershineServer.productApi.dto.AllAvailableFilterDto;
import com.evershine.EvershineServer.productApi.dto.CategoryDto;
import com.evershine.EvershineServer.productApi.dto.CategoryTypeDto;
import com.evershine.EvershineServer.productApi.dto.CategoryVariantDto;
import com.evershine.EvershineServer.productApi.entity.*;
import com.evershine.EvershineServer.productApi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class FilterService {
    private final CategoryRepository categoryRepository;
    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryVariantRepository categoryVariantRepository;

    private final BrandRepository brandRepository;
    private final SubBrandRepository subBrandRepository;
    private final GradeRepository gradeRepository;
    private final SubGradeRepository subGradeRepository;

    @Transactional(readOnly = true)
    @Cacheable("filters")
    public AllAvailableFilterDto getAllAvailableFilters(){
        List<CategoryDto> parentCategory = getFullCategoryTree();

        List<Brand>brands = brandRepository.findAll();
        List<Grade>grades = gradeRepository.findAll();
        List<SubGrade>subGrades = subGradeRepository.findAll();
        List<SubBrand>subBrands = subBrandRepository.findAll();
        AllAvailableFilterDto dto = new AllAvailableFilterDto(
                parentCategory,
                brands,
                subBrands,
                grades,
                subGrades
        );
        return dto;
    }

    public List<CategoryDto> getFullCategoryTree() {
        // Query 1: Fetch ALL Categories
        List<Category> allCategories = categoryRepository.findAll();

        // Query 2: Fetch ALL CategoryTypes
        List<CategoryType> allTypes = categoryTypeRepository.findAll();

        // Query 3: Fetch ALL CategoryVariants
        List<CategoryVariant> allVariants = categoryVariantRepository.findAll();

        // --- IN-MEMORY GROUPING ---

        // 1. Group Variants by their CategoryType ID
        // Map<CategoryTypeId, List<CategoryVariant>>
        Map<UUID, List<CategoryVariant>> variantsByTypeMap = allVariants.stream()
                .collect(Collectors.groupingBy(variant -> variant.getCategoryType().getId()));

        // 2. Group Types by their Category ID
        // Map<CategoryId, List<CategoryTypeDto>>
        Map<UUID, List<CategoryTypeDto>> typesByCategoryMap = allTypes.stream()
                .collect(Collectors.groupingBy(
                        type -> type.getCategory().getId(),
                        Collectors.mapping(type -> {
                            CategoryTypeDto typeDto = new CategoryTypeDto();
                            typeDto.setId(type.getId());
                            typeDto.setName(type.getName());
                            typeDto.setSlug(type.getSlug());
                            List<CategoryVariant> variantsForThisType = variantsByTypeMap.getOrDefault(type.getId(), Collections.emptyList());
                            typeDto.setVariants(variantsForThisType.stream()
                                    .map(v -> new CategoryVariantDto(v.getId(), v.getName(), v.getSlug()))
                                    .toList());
                            return typeDto;
                        }, Collectors.toList())
                ));


        // 3. Build the final Category DTOs
        return allCategories.stream().map(category -> {
            CategoryDto catDto = new CategoryDto();
            catDto.setId(category.getId());
            catDto.setName(category.getName());
            catDto.setSlug(category.getSlug());

            // Attach the pre-grouped types
            catDto.setTypes(typesByCategoryMap.getOrDefault(category.getId(), Collections.emptyList()));

            return catDto;
        }).collect(Collectors.toList());
    }

    public Category getCategory(UUID categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Category does not exists with id: "+categoryId)
                );
    }

    public CategoryType getCategoryType(UUID id){
        return categoryTypeRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("CategoryType does not exists with id: "+id)
                );
    }

    public CategoryVariant getCategoryVariant(UUID id){
        return categoryVariantRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("CategoryVariant does not exists with id: "+id)
                );
    }

    public Brand getBrand(UUID id){
        return brandRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Brand does not exists with id: "+id)
                );
    }

    public SubBrand getSubBrand(UUID id){
        return subBrandRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("SubBrand does not exists with id: "+id)
                );
    }

    public Grade getGrade(UUID id){
        return gradeRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Grade does not exists with id: "+id)
                );
    }

    public SubGrade getSubGrade(UUID id){
        return subGradeRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("SubGrade does not exists with id: "+id)
                );
    }
}