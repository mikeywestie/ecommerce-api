package com.mikey.ecommerce.system.bugreport;

import com.mikey.ecommerce.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

@Service
public class GitHubIssueService {

    private final RestClient restClient;

    @Value("${GITHUB_BUG_REPORT_TOKEN:}")
    private String githubToken;

    @Value("${GITHUB_BUG_REPORT_REPO:mikeywestie/ecommerce-admin-ui}")
    private String githubRepo;

    public GitHubIssueService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.github.com")
                .build();
    }

    public BugReportResponse createBugReport(BugReportRequest request) {
        if (githubToken == null || githubToken.isBlank()) {
            throw new ApiException("GitHub bug report token is not configured");
        }

        GitHubIssueRequest githubRequest = new GitHubIssueRequest(
                buildTitle(request),
                buildBody(request),
                List.of("bug", "reported-from-app")
        );

        GitHubIssueResponse githubResponse = restClient.post()
                .uri("/repos/{repo}/issues", githubRepo)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(githubRequest)
                .retrieve()
                .body(GitHubIssueResponse.class);

        if (githubResponse == null) {
            throw new ApiException("GitHub issue could not be created");
        }

        return new BugReportResponse(
                true,
                githubResponse.htmlUrl(),
                githubResponse.number()
        );
    }

    private String buildTitle(BugReportRequest request) {
        String route = valueOrDefault(request.route(), "unknown route");

        String message = valueOrDefault(request.message(), "Bug report")
                .replace("\n", " ")
                .trim();

        if (message.length() > 70) {
            message = message.substring(0, 70).trim() + "...";
        }

        return "[Bug Report] " + message + " (" + route + ")";
    }

    private String buildBody(BugReportRequest request) {
        return """
                ## Bug Report

                ### Message
                %s

                ### Steps to Reproduce
                %s

                ### Context
                | Field | Value |
                |---|---|
                | Route | `%s` |
                | User Email | `%s` |
                | User Role | `%s` |
                | Frontend Version | `%s` |
                | Frontend Commit | `%s` |
                | Frontend Build Time | `%s` |
                | Backend Version | `%s` |
                | Backend Commit | `%s` |
                | Backend Build Time | `%s` |
                | Browser | `%s` |
                | Viewport | `%s` |
                | Reported At | `%s` |

                ### Screenshot
                %s
                """.formatted(
                valueOrDefault(request.message(), "_No message provided_"),
                valueOrDefault(request.stepsToReproduce(), "_No steps provided_"),
                valueOrDefault(request.route(), "unknown"),
                valueOrDefault(request.userEmail(), "unknown"),
                valueOrDefault(request.userRole(), "unknown"),
                valueOrDefault(request.frontendVersion(), "unknown"),
                valueOrDefault(request.frontendCommit(), "unknown"),
                valueOrDefault(request.frontendBuildTime(), "unknown"),
                valueOrDefault(request.backendVersion(), "unknown"),
                valueOrDefault(request.backendCommit(), "unknown"),
                valueOrDefault(request.backendBuildTime(), "unknown"),
                valueOrDefault(request.browser(), "unknown"),
                valueOrDefault(request.viewport(), "unknown"),
                Instant.now().toString(),
                buildScreenshotSection(request.screenshot())
        );
    }

    private String buildScreenshotSection(String screenshot) {
        if (screenshot == null || screenshot.isBlank()) {
            return "_No screenshot included._";
        }

        return """
                A screenshot was captured by the application.

                > Screenshot data was included in the bug report payload but is not attached yet.
                > Next enhancement: upload screenshots as GitHub issue attachments or store externally.
                """;
    }

    private String valueOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }

    private record GitHubIssueRequest(
            String title,
            String body,
            List<String> labels
    ) {
    }

    private record GitHubIssueResponse(
            Long number,
            @com.fasterxml.jackson.annotation.JsonProperty("html_url")
            String htmlUrl
    ) {
    }
}