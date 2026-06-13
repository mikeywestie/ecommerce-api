package com.mikey.ecommerce.system.bugreport;

import jakarta.validation.constraints.NotBlank;

public record BugReportRequest(
        @NotBlank(message = "Message is required")
        String message,

        String stepsToReproduce,
        String screenshot,
        String route,
        String userEmail,
        String userRole,
        String frontendVersion,
        String frontendCommit,
        String frontendBuildTime,
        String backendVersion,
        String backendCommit,
        String backendBuildTime,
        String browser,
        String viewport
) {
}