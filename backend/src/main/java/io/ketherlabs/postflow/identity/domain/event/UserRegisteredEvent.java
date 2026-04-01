package io.ketherlabs.postflow.identity.domain.event;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userid, String email
) {

    public UserRegisteredEvent(UUID userid, String email) {
        this.userid = userid;
        this.email = email;
    }
}
