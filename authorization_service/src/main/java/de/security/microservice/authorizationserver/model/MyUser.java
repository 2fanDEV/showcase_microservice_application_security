package de.security.microservice.authorizationserver.model;

import de.security.microservice.authorizationserver.utils.Status;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom User Class
 *
 * If we want to have more details about a user
 * we can extend this class and simultaneously generate a new changelog file that
 * also creates new columns in the database for this service
 *
 * https://www.baeldung.com/spring-security-authentication-with-a-database
 * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/user-details.html
 *
 * This class is implementing the same schema as {@link org.springframework.security.core.userdetails.User}
 * but has two 2 more variables which should be failedAttempts and lockedUntil
 * to keep track of the login attempts and to lock a user for a specific timeframe
 *
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class MyUser implements Serializable {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "username",  cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Authorities> authorities;

    /**
     * @transient to tell JPA not to expect a field in the database
     * @Getter(AccessLevel.NONE) to tell lombok that it shouldn't generate
     * a getter so we can create a custom one ourselves
     */
    @Transient
    @Getter(AccessLevel.NONE)
    Set<GrantedAuthority> grantedAuthoritySet;

    @Column(name = "status")
    private Status status;

    private boolean isAccountNonLocked;

    private boolean isAccountNonExpired;

    private boolean isCredentialsNonExpired;

    @Column(name = "enabled")
    private boolean isEnabled;

    private Long failedAttempts;

    private Date lockedUntil;

    public Collection<? extends GrantedAuthority> getGrantedAuthoritySet() {
        this.grantedAuthoritySet = new HashSet<>();
        for(Authorities authorities : authorities)
        {
            this.grantedAuthoritySet.add(new SimpleGrantedAuthority(authorities.getGrantedAuthority()));
        }
        return this.grantedAuthoritySet;
    }
}
