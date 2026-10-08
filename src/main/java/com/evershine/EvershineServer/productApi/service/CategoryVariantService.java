package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.FilterOptionNotFoundException;
import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.entity.CategoryType;
import com.evershine.EvershineServer.productApi.entity.CategoryVariant;
import com.evershine.EvershineServer.productApi.repository.CategoryTypeRepository;
import com.evershine.EvershineServer.productApi.repository.CategoryVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryVariantService {

    private final CategoryVariantRepository categoryVariantRepository;
    private final CategoryTypeRepository categoryTypeRepository;


    @Transactional
    public CatalogueResponseDto addCategoryVariant(
            UUID parentId,
            CatalogueRequestDto dto
    ) {

        // parentId = CategoryType ID
        CategoryType categoryType = categoryTypeRepository.findById(parentId)
                .orElseThrow(() ->
                        new FilterOptionNotFoundException(
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
                        new FilterOptionNotFoundException(
                                "CategoryVariant does not exist with id: " + id
                        )
                );

        return toCatalogueResponseDto(variant);
    }


    @Transactional
    public CatalogueResponseDto updateCategoryVariantById(
            UUID id,
            CatalogueRequestDto dto
    ) {

        CategoryVariant variant = categoryVariantRepository.findById(id)
                .orElseThrow(() ->
                        new FilterOptionNotFoundException(
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