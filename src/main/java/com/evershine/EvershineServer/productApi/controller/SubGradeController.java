package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.service.SubGradeService;
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
@RequestMapping("/api/sub-grades")
@RequiredArgsConstructor
@Tag(name = "SubGrade service", description = """
        Note: SubGrade name is Unique, duplicate is not allowed
        Admin:
        Can add, soft delete brand, soft delete will soft delete associated SubGrade products,
        fetch all SubGrade available and update SubGrade name
        
        User:
         fetch active SubGrade
        """)
public class SubGradeController {
    private final SubGradeService subGradeService;

    @Operation(summary = "May throw DuplicateBrandException", description = "Name should be unique, try duplicate one")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SubGrade successfully created",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g. name blank or > 50 chars)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: SubGrade name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<FilterResponseDto> addSubGrade(
            @RequestParam
            @Parameter(description = "send name of SubGrade in param", required = true, schema = @Schema(maxLength = 50,minLength = 1))
            @Size(max = 50, message = "SubGrade name cannot exceed 50 characters")
            @NotEmpty(message = "Name should not be empty!")
            String name){
        return ResponseEntity.ok(subGradeService.addSubGrade(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SubGrade by ID", description = "Fetches a single SubGrade details matching the provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SubGrade details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No SubGrade exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )

    })
    public ResponseEntity<FilterResponseDto> getSubGrade(
            @Parameter(description = "The unique UUID of the SubGrade", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(subGradeService.getSubGrade(id));
    }

    @GetMapping
    @Operation(summary = "Get all SubGrade", description = "Retrieves a comprehensive list of all SubGrade (including active and deactivated ones) for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "List of SubGrade retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class))))
    public ResponseEntity<List<FilterResponseDto>> getAllSubGrade() {
        return ResponseEntity.ok(subGradeService.getAllSubGrade());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active SubGrade", description = "Retrieves a list containing only SubGrade that are marked as active.")
    @ApiResponse(
            responseCode = "200",
            description = "List of active SubGrade retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class)))
    )
    public ResponseEntity<List<FilterResponseDto>> getAllActiveSubGrade() {
        return ResponseEntity.ok(subGradeService.getAllActiveSubGrades());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing SubGrade name", description = "Updates the target SubGrade's name. Validates against name duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SubGrade successfully updated",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: SubGrade ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new SubGrade name is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<FilterResponseDto> updateSubGrade(
            @Parameter(description = "The UUID of the SubGrade to change", required = true) @PathVariable UUID id,
            @RequestParam
            @Size(max = 50, message = "SubGrade name cannot exceed 50 characters")
            @NotEmpty(message = "SubGrade name should not be empty!")
            String newName) {
        return ResponseEntity.ok(subGradeService.updateSubGrade(newName,id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a SubGrade", description = "Performs a soft-delete by deactivating the SubGrade and marking all its associated products as UNAVAILABLE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SubGrade deactivated successfully and associated products updated",
                    content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "404", description = "Not Found: No SubGrade matching the provided ID was located",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> deleteSubGrade(
            @Parameter(description = "The unique UUID of the SubGrade to deactivate", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(subGradeService.deleteSubGrade(id));
    }
}
