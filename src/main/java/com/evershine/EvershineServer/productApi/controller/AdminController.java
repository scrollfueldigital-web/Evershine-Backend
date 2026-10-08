package com.evershine.EvershineServer.productApi.controller;

import com.evershine.EvershineServer.productApi.dto.ProductAdminDashboardDTO;
import com.evershine.EvershineServer.productApi.service.AdminService;
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


@Validated // so all validation will work
@RestController
@RequestMapping("/api/admin-dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = """
        Get all Required data for admin such as total product count, active, unavailable or
        based on specific filter type
        """)
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Get single admin response", description = "Retrieves dashboard data for administration.")
    @ApiResponse(
            responseCode = "200",
            description = "Dashboard retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductAdminDashboardDTO.class)))
    public ResponseEntity<ProductAdminDashboardDTO> getAllBrands() {
        return ResponseEntity.ok(adminService.getDashboard());
    }
}
