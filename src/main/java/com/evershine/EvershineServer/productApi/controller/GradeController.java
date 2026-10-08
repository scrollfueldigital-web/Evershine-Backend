package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.ErrorResponseDto;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.service.GradeService;
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
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Grade service", description = """
        Note: Grade name is Unique, duplicate is not allowed
        Admin:
        Can add, soft delete brand, soft delete will soft delete associated Grade products,
        fetch all Grade available and update Grade name
        
        User:
         fetch active Grade
        """)
public class GradeController {

    private final GradeService gradeService;

    @Operation(summary = "May throw DuplicateBrandException",
            description = "Name should be unique, try duplicate one")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Grade successfully created",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure (e.g. name blank or > 50 chars)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: Grade name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<FilterResponseDto> addGrade(
            @RequestParam
            @Parameter(description = "send name of grade in param", required = true, schema = @Schema(maxLength = 50,minLength = 1))
            @Size(max = 50, message = "Grade name cannot exceed 50 characters")
            @NotEmpty(message = "Name should not be empty!")
            String name){
        return ResponseEntity.ok(gradeService.addGrade(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Grade by ID", description = "Fetches a single Grade details matching the provided UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Grade details successfully retrieved",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: No grade exists with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<FilterResponseDto> getGrade(
            @Parameter(description = "The unique UUID of the Grade", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(gradeService.getGrade(id));
    }

    @GetMapping
    @Operation(summary = "Get all Grades", description = "Retrieves a comprehensive list of all Grade (including active and deactivated ones) for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "List of grade retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class))))
    public ResponseEntity<List<FilterResponseDto>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrade());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active Grade", description = "Retrieves a list containing only grade that are marked as active.")
    @ApiResponse(
            responseCode = "200",
            description = "List of active grade retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilterResponseDto.class)))
    )
    public ResponseEntity<List<FilterResponseDto>> getAllActiveGrades() {
        return ResponseEntity.ok(gradeService.getAllActiveGrades());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Grade name", description = "Updates the target grade's name. Validates against name duplicates before processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grade successfully updated",
                    content = @Content(schema = @Schema(implementation = FilterResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found: Grade ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: The new Grade name is already taken by another entity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<FilterResponseDto> updateBrand(
            @Parameter(description = "The UUID of the Grade to change", required = true) @PathVariable UUID id,
            @RequestParam
            @Size(max = 50, message = "Grade name cannot exceed 50 characters")
            @NotEmpty(message = "Grade name should not be empty!")
            String newName) {
        return ResponseEntity.ok(gradeService.updateGrade(newName,id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a grade", description = "Performs a soft-delete by deactivating the grade and marking all its associated products as UNAVAILABLE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grade deactivated successfully and associated products updated",
                    content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "404", description = "Not Found: No grade matching the provided ID was located",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> deleteBrand(
            @Parameter(description = "The unique UUID of the grade to deactivate", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(gradeService.deleteGrade(id));
    }
}
