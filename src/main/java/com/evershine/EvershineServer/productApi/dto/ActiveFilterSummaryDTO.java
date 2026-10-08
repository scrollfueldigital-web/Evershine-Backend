package com.evershine.EvershineServer.productApi.dto;

import java.util.UUID;

public record ActiveFilterSummaryDTO(
        UUID id,
        String name,
        boolean active,
        long productCount
) {}
