package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@Validated // so all validation will work
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brand service", description = """
        Note: Brand name is Unique, duplicate is not allowed
        Admin:
        Can add, soft delete brand, soft delete will soft delete associated brands products,
        fetch all brands available and update brand name
        
        User:
         fetch active brands
        """)
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "May throw DuplicateBrandException", description = "Name should be unique, try duplicate one")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Brand successfully created",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g. name blank or > 50 chars)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Brand name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<FilterResponseDto> addBrand(
            @RequestParam
            @Parameter(description = "send name of brand in param",
                    required = true, schema = @Schema(maxLength = 50,minLength = 1))
            @Size(max = 50, message = "Brand name cannot exceed 50 characters")
            @NotEmpty(message = "Name should not be empty!")
            String name){
        return ResponseEntity.ok(brandService.addBrand(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Brand by ID", description = "Fetches a single brand details matching the provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Brand details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No brand exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )

    })
    public ResponseEntity<FilterResponseDto> getBrand(
            @Parameter(description = "The unique UUID of the brand", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrand(id));
    }

    @GetMapping
    @Operation(summary = "Get all Brands", description = "Retrieves a comprehensive list of all brands (including active and deactivated ones) for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "List of brands retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class))))
    public ResponseEntity<List<FilterResponseDto>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active Brands", description = "Retrieves a list containing only brands that are marked as active.")
    @ApiResponse(
            responseCode = "200",
            description = "List of active brands retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class)))
    )
    public ResponseEntity<List<FilterResponseDto>> getAllActiveBrands() {
        return ResponseEntity.ok(brandService.getAllActiveBrands());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Brand name", description = "Updates the target brand's name. Validates against name duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand successfully updated",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: Brand ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new brand name is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<FilterResponseDto> updateBrand(
            @Parameter(description = "The UUID of the brand to change", required = true) @PathVariable UUID id,
            @RequestParam
            @Size(max = 50, message = "Brand name cannot exceed 50 characters")
            @NotEmpty(message = "Brand name should not be empty!")
            String newName) {
        return ResponseEntity.ok(brandService.updateBrand(newName, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a Brand", description = "Performs a soft-delete by deactivating the brand and marking all its associated products as UNAVAILABLE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand deactivated successfully and associated products updated",
                    content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "404", description = "Not Found: No brand matching the provided ID was located",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> deleteBrand(
            @Parameter(description = "The unique UUID of the brand to deactivate", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }
}
