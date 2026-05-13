package com.mikey.ecommerce.dashboard;

import com.mikey.ecommerce.dto.dashboard.DashboardSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for administrative dashboard metrics.
 *
 * <p>This controller provides summary statistics used by the admin dashboard,
 * including aggregated business and operational metrics such as:</p>
 * <ul>
 *     <li>Total number of products</li>
 *     <li>Total number of orders</li>
 *     <li>Total number of payments</li>
 *     <li>Total revenue</li>
 *     <li>Inventory and operational insights</li>
 * </ul>
 *
 * <p>The dashboard data is aggregated by {@link DashboardService} and returned
 * as a single response optimized for frontend dashboard consumption.</p>
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(
        name = "Dashboard",
        description = "Administrative dashboard endpoints for retrieving business and operational summary metrics."
)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves a summary of dashboard metrics.
     *
     * @return dashboard summary response containing aggregated metrics
     */
    @Operation(
            summary = "Get dashboard summary",
            description = "Returns aggregated business metrics used by the admin dashboard, including totals for products, orders, payments, revenue, and inventory."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard summary retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DashboardSummaryResponse.class)
                    )
            )
    })
    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}