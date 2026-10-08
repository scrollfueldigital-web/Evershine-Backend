package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.service.SubBrandService;
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
@RequestMapping("/api/sub-brands")
@RequiredArgsConstructor
@Tag(name = "Sub-Brand service", description = """
        Note: Sub-Brand name is Unique, duplicate is not allowed
        Admin:
        Can add, soft delete sub-brand, soft delete will soft delete associated sub-brands related products,
        fetch all sub-brands available and update sub-brand name
        
        User:
         fetch active sub-brands
        """)
public class SubBrandController {
    private final SubBrandService subBrandService;

    @Operation(summary = "May throw DuplicateBrandException", description = "Name should be unique, try duplicate one on test")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sub-Brand successfully created",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g. name blank or > 50 chars)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Sub-Brand name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<FilterResponseDto> addBrand(
            @RequestParam @Parameter(description = "send name of sub-brand in param", required = true, schema = @Schema(maxLength = 50,minLength = 1))
            @Size(max = 50, message = "SubBrand name cannot exceed 50 characters")
            @NotEmpty(message = "Name should not be empty!") String name){
        return ResponseEntity.ok(subBrandService.addSubBrand(name));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get Sub-Brand by ID", description = "Fetches a single sub-brand details matching the provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "sub-Brand details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No sub-brand exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<FilterResponseDto> getBrand(
            @Parameter(description = "The unique UUID of the sub-brand", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(subBrandService.getSubBrand(id));
    }

    @GetMapping
    @Operation(summary = "Get all SubBrands", description = "Retrieves a comprehensive list of all sub-brands (including active and deactivated ones) for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "List of sub-brands retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class))))
    public ResponseEntity<List<FilterResponseDto>> getAllBrands() {
        return ResponseEntity.ok(subBrandService.getAllSubBrands());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active SubBrands", description = "Retrieves a list containing only sub-brands that are marked as active.")
    @ApiResponse(
            responseCode = "200",
            description = "List of active sub-brands retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class)))
    )
    public ResponseEntity<List<FilterResponseDto>> getAllActiveBrands() {
        return ResponseEntity.ok(subBrandService.getAllActiveSubBrands());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing SubBrand name", description = "Updates the target sub-brand's name. Validates against name duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SubBrand successfully updated",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: SubBrand ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new sub-brand name is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<FilterResponseDto> updateBrand(
            @Parameter(description = "The UUID of the sub-brand to change", required = true) @PathVariable UUID id,
            @RequestParam
            @Size(max = 50, message = "Brand name cannot exceed 50 characters")
            @NotEmpty(message = "Brand name should not be empty!")
            String newName) {
        return ResponseEntity.ok(subBrandService.updateBrand(newName,id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a SubBrand", description = "Performs a soft-delete by deactivating the sub-brand and marking all its associated products as UNAVAILABLE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SubBrand deactivated successfully and associated products updated",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Not Found: No sub-brand matching the provided ID was located",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> deleteBrand(
            @Parameter(description = "The unique UUID of the sub-brand to deactivate", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(subBrandService.deleteSubBrand(id));
    }
}
