package fr.itemmanage.itemmanage.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        List<String> messages,
        String path
) {}