package com.swozo.orchestrator.cloud.software.runner;

public record CommandResult(int returnCode, String output, String errors) {
}
