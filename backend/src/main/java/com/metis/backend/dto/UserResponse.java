package com.metis.backend.dto;

import com.metis.backend.entity.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {}
