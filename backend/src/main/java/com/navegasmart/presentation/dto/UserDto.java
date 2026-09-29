package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.AuthenticatedUser;

public record UserDto(Long id, String username, String fullName) {

    public static UserDto from(AuthenticatedUser user) {
        return new UserDto(user.id(), user.username(), user.fullName());
    }
}
