package com.evershine.EvershineServer.productApi.dto;

import com.evershine.EvershineServer.productApi.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AllAvailableFilterDto {
    private List<CategoryDto>categories;
    private List<Brand> brands;
    private List<SubBrand>subBrands;
    private List<Grade>grades;
    private List<SubGrade>subGrades;
}
