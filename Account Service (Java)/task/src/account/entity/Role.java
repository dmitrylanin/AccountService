package account.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String roleName;
    @Transient
    @ManyToMany(mappedBy = "rolesOfUser")
    private Set<AppUser> users;

    public Role(){};

    @JsonValue
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String role){this.roleName = role;}

    @Override
    public String getAuthority() {
        return getRoleName();
    }
}