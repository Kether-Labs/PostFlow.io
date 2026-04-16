package io.ketherlabs.postflow.identity.domain.event;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userid, String email, String token
) {

    public UserRegisteredEvent(UUID userid, String email, String token) {
        this.userid = userid;
        this.email = email;
        this.token = token;
    }
}
