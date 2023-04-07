package de.security.microservice.authorizationserver.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Authorities Class
 *
 * This is the class which is responsible
 * for the roles a user might be able to have
 *
 * The behaviour is a map between a username and the corresponding
 * authority
 *
 * This authorization schema was
 * build with the user schema defined by the spring documentation
 *
 * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html#servlet-authentication-jdbc-schema-user
 */
@Entity
@Table(name = "authorities")
@IdClass(Authorities.class)
public class Authorities implements Serializable {

    @Id
    @Column(name = "username")
    private String username;
    @Id
    @Column(name = "authority")
    private String grantedAuthority;

    public Authorities(String username, String authority) {
        this.username = username;
        this.grantedAuthority = authority;
    }

    public Authorities() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGrantedAuthority() {
        return grantedAuthority;
    }

    public void setGrantedAuthority(String grantedAuthority) {
        this.grantedAuthority = grantedAuthority;
    }
}
