package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.service.CategoryTypeService;
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
@RequestMapping("/api/category-types")
@RequiredArgsConstructor
@Tag(name = "CategoryType service", description = """
        Note: CategoryType name and slug is Unique, duplicate is not allowed
        Admin:
        Can add, fetch all CategoryType, by id available and update Category name n slug
        
        User:
         fetch active Category
        """)
public class CategoryTypeController {
    private final CategoryTypeService categoryTypeService;

    @Operation(summary = "May throw DuplicateFilerException", description = "It will take parent category id, Name,slug should be unique, try duplicate one")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CategoryType successfully created",
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
    @PostMapping("/{id}")
    public ResponseEntity<CatalogueResponseDto> addCategoryType(
            @Valid @RequestBody CatalogueRequestDto catalogueRequestDto,
            @Parameter(description = "The unique UUID of the CategoryType", required = true)
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(categoryTypeService.addCategoryType(id,catalogueRequestDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get CategoryType by ID", description = "Fetches a CategoryType details matching to provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CategoryType details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = CatalogueResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No Category exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )

    })
    public ResponseEntity<CatalogueResponseDto> getBrand(
            @Parameter(description = "The unique UUID of the CategoryType", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(categoryTypeService.getCategoryTypeById(id));
    }


    @GetMapping
    @Operation(summary = "Get all CategoryType", description = "Retrieves a comprehensive list of all CategoryType.")
    @ApiResponse(
            responseCode = "200",
            description = "List of CategoryType retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogueResponseDto.class)))
    )
    public ResponseEntity<List<CatalogueResponseDto>> getAllCategoryTypes() {
        return ResponseEntity.ok(categoryTypeService.getAllCategoryTypes());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing CategoryType name", description = "Updates the target CategoryType's name, slug. Validates against name and slug for duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CategoryType successfully updated",
                    content = @Content(schema = @Schema(implementation = CatalogueResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: CategoryType ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new CategoryType name or slug is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CatalogueResponseDto> updateCategoryById(@Valid @RequestBody CatalogueRequestDto catalogueRequestDto, @PathVariable UUID id) {
        return ResponseEntity.ok(
                categoryTypeService.updateCategoryTypeById(id,catalogueRequestDto));
    }
}
