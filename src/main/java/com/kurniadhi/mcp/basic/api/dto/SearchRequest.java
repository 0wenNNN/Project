package com.kurniadhi.mcp.basic.api.dto;

public record SearchRequest(String query, int limit) {
    public SearchRequest {
        if (limit <= 0) limit = 10;
    }
}
