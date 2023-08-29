package org.quizstoradge.director.dto;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record GameInfo(
        String id,
        String userId,
        String sourceId,
        Instant start,
        Instant end
) {
}
