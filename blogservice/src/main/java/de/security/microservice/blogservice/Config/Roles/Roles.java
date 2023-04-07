package de.security.microservice.blogservice.Config.Roles;

/**
 * We want to have an easy way in the securityFilterChain to see
 * which Roles are allowed to make a request to a specific path
 * and this class makes it easy to access them.
 */
public class Roles {

    //logged in as user
    public static final String USER = "ROLE_USER";
    //logged in as admin
    public static final String ADMIN = "ROLE_ADMIN";
    //not logged in at all
    public static final String ANONYMOUS = "ROLE_ANON";

}
