package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.AllAvailableFilterDto;
import com.evershine.EvershineServer.productApi.service.FilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated // so all validation will work
@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
@Tag(name = "filter service", description = """
         fetch categories, active filters{Grade, Brand, Sub-Brand, Sub-Grade}
        """)
public class FilterController {
    private final FilterService filterService;

    @GetMapping
    @Operation(summary = "Get all Available Filters", description = "Retrieves a comprehensive list of all Filter (including active ones) for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "List of filter retrieved successfully",
            content = @Content(schema = @Schema(implementation = AllAvailableFilterDto.class)))
    public ResponseEntity<AllAvailableFilterDto> getAllFilters() {
        return ResponseEntity.ok(filterService.getAllAvailableFilters());
    }
}
