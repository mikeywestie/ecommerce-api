package com.mikey.ecommerce.system;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/system")
public class BuildInfoController {

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    @Value("${spring.application.name:ecommerce-api}")
    private String applicationName;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Value("${RENDER_GIT_COMMIT:${GIT_COMMIT:local}}")
    private String gitCommit;

    @Value("${RENDER_GIT_BRANCH:${GIT_BRANCH:local}}")
    private String gitBranch;

    public BuildInfoController(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    @GetMapping("/build-info")
    public BuildInfoResponse getBuildInfo() {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();

        String version = buildProperties != null
                ? buildProperties.getVersion()
                : "local";

        String buildTime = buildProperties != null && buildProperties.getTime() != null
                ? buildProperties.getTime().toString()
                : Instant.now().toString();

        String commit = cleanValue(gitCommit, "local");
        String branch = cleanValue(gitBranch, "local");

        return new BuildInfoResponse(
                applicationName,
                version,
                activeProfile,
                branch,
                commit,
                abbreviateCommit(commit),
                buildTime
        );
    }

    private String cleanValue(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }

    private String abbreviateCommit(String commit) {
        if (commit == null || commit.isBlank() || "local".equalsIgnoreCase(commit)) {
            return "local";
        }

        return commit.length() <= 7 ? commit : commit.substring(0, 7);
    }
}
