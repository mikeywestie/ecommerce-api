package com.mikey.ecommerce.system;

public record BuildInfoResponse(
    String application,
    String version,
    String environment,
    String branch,
    String commit,
    String commitShort,
    String buildTime) {}
