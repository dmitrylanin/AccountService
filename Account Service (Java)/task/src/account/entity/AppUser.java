package account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String lastname;
    private String email;
    @JsonIgnore
    private boolean isBlocked;
    @JsonIgnore
    private int bruteForceCount;
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> rolesOfUser = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void getAllRoles() {
        for (Role role : rolesOfUser){
            System.out.println(role.getRoleName());
        }
    }
    @JsonProperty("roles")
    public List<Role> getOrderedRoles(){
        List<Role> sortedRoles = new ArrayList<>(rolesOfUser);

        Collections.sort(sortedRoles, new Comparator<Role>() {
            @Override
            public int compare(Role r1, Role r2) {
                return r1.getRoleName().compareTo(r2.getRoleName());
            }
        });

        return sortedRoles;
    }
    public Set<Role> getRoles() {
        return rolesOfUser;
    }
    public void setRoles(Set<Role> roles) {
        for (Role nRole: roles){
            rolesOfUser.add(nRole);
        }
    }
    public boolean isBlocked() {
        return isBlocked;
    }
    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
    public int getBruteForceCount() {
        return bruteForceCount;
    }
    public void setBruteForceCount(int bruteForceCount) {
        this.bruteForceCount = bruteForceCount;
    }

    public List<String> getOrderedRoleNames(){
        return getOrderedRoles().stream()
                .map(Role :: getRoleName)
                .toList();
    }
}
