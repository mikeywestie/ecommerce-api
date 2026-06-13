package com.mikey.ecommerce.system.bugreport;

public record BugReportResponse(
        boolean success,
        String issueUrl,
        Long issueNumber
) {
}