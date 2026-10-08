package com.evershine.EvershineServer.productApi.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductAdminDashboardDTO {

    long totalProducts;
    long activeProducts;
    long unavailableProducts;

    List<ActiveFilterSummaryDTO> brands;
    List<ActiveFilterSummaryDTO> subBrands;
    List<ActiveFilterSummaryDTO> grades;
    List<ActiveFilterSummaryDTO> subGrades;

    List<FilterSummaryDTO> categories;
    List<FilterSummaryDTO> categoryTypes;
    List<FilterSummaryDTO> categoryVariants;
    // add all filter active, deactivate
}
