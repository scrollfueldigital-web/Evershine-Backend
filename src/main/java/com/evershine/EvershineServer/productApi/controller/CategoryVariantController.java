package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.CatalogueRequestDto;
import com.evershine.EvershineServer.productApi.dto.CatalogueResponseDto;
import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.service.CategoryVariantService;
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

@Validated
@RestController
@RequestMapping("/api/category-variants")
@RequiredArgsConstructor
@Tag(
        name = "CategoryVariant service",
        description = """
                Note: CategoryVariant name and slug are unique.
                Duplicate values are not allowed.

                Admin:
                Can add, fetch all, fetch by ID and update CategoryVariant name and slug.

                User:
                Can fetch CategoryVariant.
                """
)
public class CategoryVariantController {

    private final CategoryVariantService categoryVariantService;


    @Operation(
            summary = "Create CategoryVariant",
            description = """
                    Creates a CategoryVariant under the provided CategoryType ID.

                    The parent ID must be an existing CategoryType ID.
                    Name and slug must be unique.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CategoryVariant successfully created",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CatalogueResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Validation failure.

                            Name: not empty, minimum 2 characters, maximum 100 characters.
                            Slug: not empty, minimum 2 characters, maximum 100 characters.
                            """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Parent CategoryType not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "CategoryVariant name or slug already exists",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            )
    })
    @PostMapping("/{parentId}")
    public ResponseEntity<CatalogueResponseDto> addCategoryVariant(
            @Valid @RequestBody CatalogueRequestDto catalogueRequestDto,
            @Parameter(
                    description = "UUID of the parent CategoryType",
                    required = true
            )
            @PathVariable UUID parentId
    ) {

        return ResponseEntity.ok(
                categoryVariantService.addCategoryVariant(
                        parentId,
                        catalogueRequestDto
                )
        );
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get CategoryVariant by ID",
            description = "Fetches a CategoryVariant matching the provided UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CategoryVariant successfully retrieved",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CatalogueResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "CategoryVariant not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            )
    })
    public ResponseEntity<CatalogueResponseDto> getCategoryVariant(
            @Parameter(
                    description = "Unique UUID of the CategoryVariant",
                    required = true
            )
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                categoryVariantService.getCategoryVariantById(id)
        );
    }


    @GetMapping
    @Operation(
            summary = "Get all CategoryVariants",
            description = "Retrieves all CategoryVariants."
    )
    @ApiResponse(
            responseCode = "200",
            description = "CategoryVariants retrieved successfully",
            content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = CatalogueResponseDto.class
                            )
                    )
            )
    )
    public ResponseEntity<List<CatalogueResponseDto>> getAllCategoryVariants() {

        return ResponseEntity.ok(
                categoryVariantService.getAllCategoryVariants()
        );
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Update CategoryVariant",
            description = """
                    Updates the CategoryVariant name and slug.

                    The parent CategoryType is not changed.
                    Name and slug are checked for duplicates.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CategoryVariant successfully updated",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CatalogueResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "CategoryVariant not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "CategoryVariant name or slug already exists",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )
            )
    })
    public ResponseEntity<CatalogueResponseDto> updateCategoryVariantById(
            @Valid @RequestBody CatalogueRequestDto catalogueRequestDto,
            @Parameter(
                    description = "Unique UUID of the CategoryVariant",
                    required = true
            )
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                categoryVariantService.updateCategoryVariantById(
                        id,
                        catalogueRequestDto
                )
        );
    }
}