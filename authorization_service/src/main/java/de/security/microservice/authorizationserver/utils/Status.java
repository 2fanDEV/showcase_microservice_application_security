package de.security.microservice.authorizationserver.utils;

/**
 * Status ENUM so we could if needed expand on registering a user
 * and if implemented at some point insert a
 * "registered" field, that gets changed to verified once the
 * user verifies that the email belongs to them (e.g: Sent out an email that includes a link that
 * needs to be clicked to verify the account)
 *
 */
public enum Status {
    VERIFIED("verified"),
    ;
    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
