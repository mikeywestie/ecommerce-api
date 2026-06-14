package com.mikey.ecommerce.system.bugreport;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/bug-reports")
public class BugReportController {

  private final GitHubIssueService gitHubIssueService;

  public BugReportController(GitHubIssueService gitHubIssueService) {
    this.gitHubIssueService = gitHubIssueService;
  }

  @PostMapping
  public BugReportResponse createBugReport(@Valid @RequestBody BugReportRequest request) {
    return gitHubIssueService.createBugReport(request);
  }
}
