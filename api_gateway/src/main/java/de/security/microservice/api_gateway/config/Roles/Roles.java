package de.security.microservice.api_gateway.config.Roles;


/**
 * Declaring a class where the 3 possible roles that are
 * stored in the JWT Token are listed and accessible
 * since this is a public class
 */
public class Roles {

    //logged in as user
    public static final String USER = "ROLE_USER";
    //logged in as admin
    public static final String ADMIN = "ROLE_ADMIN";
    //not logged in at all
    public static final String ANONYMOUS = "ROLE_ANON";

}
