package io.ketherlabs.postflow.identity.domain.event;

import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;

public record UserActivatedEvent(
        UserStatus status
) {

    public UserActivatedEvent(UserStatus status) {
        this.status = status;
    }
}
