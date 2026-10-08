package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated // so all validation will work
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category service", description = """
        Note: Category name is Unique, duplicate is not allowed
                same for category slug is also unique
        Admin:
        Can add, fetch all Category available and update Category name n slug
        
        User:
         fetch active Category
        """)
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "May throw DuplicateFilerException", description = "Name,slug should be unique, try duplicate one")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category successfully created",
                    content = @Content(schema = @Schema(implementation = CatalogueResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g. name blank or > 50 chars)"+
                            """
                                    Name : not empty, min chars allowed 2, max allowed 100
                                    slug: not empty, min chars allowed 2, max allowed 100
                                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Category name or slug already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<CatalogueResponseDto> addBrand(
            @Valid @RequestBody CatalogueRequestDto catalogueRequestDto
            ){
        return ResponseEntity.ok(categoryService.addCategory(catalogueRequestDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category by ID", description = "Fetches a Category details matching to provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = CatalogueResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No Category exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )

    })
    public ResponseEntity<CatalogueResponseDto> getBrand(
            @Parameter(description = "The unique UUID of the Category", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    @Operation(summary = "Get all Category", description = "Retrieves a comprehensive list of all Category.")
    @ApiResponse(
            responseCode = "200",
            description = "List of Category retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogueResponseDto.class)))
    )
    public ResponseEntity<List<CatalogueResponseDto>> getAllBrands() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Category name", description = "Updates the target Category's name, slug. Validates against name and slug for duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category successfully updated",
                    content = @Content(schema = @Schema(implementation = CatalogueResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: Category ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new Category name or slug is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CatalogueResponseDto> updateBrand(@Valid @RequestBody CatalogueRequestDto catalogueRequestDto, @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.updateCategoryById(id,catalogueRequestDto));
    }
}
