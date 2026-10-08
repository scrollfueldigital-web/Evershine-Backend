package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO;
import com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO;
import com.evershine.EvershineServer.productApi.dto.ProductAdminDashboardDTO;
import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Grade;
import com.evershine.EvershineServer.productApi.entity.SubGrade;
import com.evershine.EvershineServer.productApi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final SubBrandRepository subBrandRepository;
    private final GradeRepository gradeRepository;
    private final SubGradeRepository subGradeRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryVariantRepository categoryVariantRepository;

    public ProductAdminDashboardDTO getDashboard() {

        long total = productRepository.count();

        long active =
                productRepository.countByProductStatus(ProductStatus.ACTIVE);

        long unavailable =
                productRepository.countByProductStatus(ProductStatus.UNAVAILABLE);

        List<ActiveFilterSummaryDTO> brands =
                brandRepository.getBrandSummaries();

        List<ActiveFilterSummaryDTO> subBrands =
                subBrandRepository.getSubBrandSummaries();

        List<ActiveFilterSummaryDTO> grades =
                gradeRepository.getGradeSummaries();

        List<ActiveFilterSummaryDTO> subGrades =
                subGradeRepository.getSubGradeSummaries();

        List<FilterSummaryDTO> categories =
                categoryRepository.getCategorySummaries();

        List<FilterSummaryDTO> categoryTypes =
                categoryTypeRepository.getCategoryTypeSummaries();

        List<FilterSummaryDTO> categoryVariants =
                categoryVariantRepository.getCategoryVariantSummaries();

        return new ProductAdminDashboardDTO(
                total,
                active,
                unavailable,
                brands,
                subBrands,
                grades,
                subGrades,
                categories,
                categoryTypes,
                categoryVariants
        );
    }
}
