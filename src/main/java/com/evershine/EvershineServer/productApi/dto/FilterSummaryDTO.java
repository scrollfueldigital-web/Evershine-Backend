package com.evershine.EvershineServer.productApi.dto;

import java.util.UUID;

public record FilterSummaryDTO(
        UUID id,
        String name,
        long productCount
) {}
