package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.*;
import com.evershine.EvershineServer.productApi.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product service", description = """
        Note: Product slug is Unique, duplicate is not allowed
        Admin:
        Can add, soft delete Product,
        fetch all Product available and update Product only no parents change allowed
        
        User:
         fetch active Product
        """)
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "May throw DuplicateFilterException", description = "Slug should be unique, try duplicate one")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully created",
                    content = @Content(schema = @Schema(implementation = ProductLightResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g. name blank or > 150 chars)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Product.slug name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<ProductLightResponseDto> addProduct(
            @Valid @RequestBody ProductRequestDto productRequestDto
            ){
        return ResponseEntity.ok(productService.addProduct(productRequestDto));
    }

    @GetMapping("/all-light-products")
    @Operation(summary = "Get Product meta data", description = "Fetches all products meta data")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = ProductMetaDataDto.class))
            )

    })
    public ResponseEntity<List<ProductMetaDataDto>> getProductsMetaData() {
        return ResponseEntity.ok(productService.getAllMetaProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product by ID", description = "Fetches a single product details matching the provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = ProductLightResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No product exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )

    })
    public ResponseEntity<ProductLightResponseDto> getProductsById(
            @Parameter(description = "The unique UUID of the product", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get Product by its slug", description = "Fetches a single Product details matching the provided slug.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = ProductLightResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No Product exists with the provided slug",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )

    })
    public ResponseEntity<ProductLightResponseDto> getProductBySlug(
            @Parameter(description = "Enter Slug for product", required = true)
            @PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }

    @GetMapping
    @Operation(summary = "Get all Products", description = "Retrieves a comprehensive list of all Products (including all filters) for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "List of Products retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductLightResponseDto.class)))
    )
    public ResponseEntity<List<ProductLightResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/status/active")
    @Operation(summary = "Get all products by status = active", description = "Retrieves a list of products by active status.")
    @ApiResponse(
            responseCode = "200",
            description = "List of active products retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductMetaDataDto.class)))
    )
    public ResponseEntity<List<ProductMetaDataDto>> getAllActiveProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }


    @Operation(summary = "Update an existing Product",
            description = "Updates the targeted product's. Validates against slug duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductLightResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: Product ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new Product slug is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductLightResponseDto> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDto productRequestDto){
        return ResponseEntity.ok(productService.updateProductById(id, productRequestDto));
    }

    @Operation(summary = "Update an existing Product, send status in path variable if want to activate it, to make it unavailable can send unavailable to keep empty its optional",
            description = "Updates the targeted product's status, it can be ACTIVE/UNAVAILABLE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductLightResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: Product ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/update/{id}/status/{status}")
    public ResponseEntity<ProductLightResponseDto> updateProductStatus(@PathVariable UUID id, @PathVariable(required = false) String status){
        return ResponseEntity.ok(productService.setProductStatus(id,status));
    }

    @Operation(summary = "Delete completely an existing Product",
            description = "delete the targeted product's.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product successfully deleted",
                    content = @Content(schema = @Schema(implementation = ProductLightResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: Product ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new Product slug is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id){
        productService.deleteProductById(id);
        return ResponseEntity.ok("Product has been deleted completely with id: "+id);
    }


}
