package account.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "id",
        "name",
        "lastname",
        "email",
        "roles"
})
public class UserData {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("email")
    private String email;
    @JsonProperty("roles")
    private List<String> roles;

    public UserData(int id, String name, String lastname, String email, List<String> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }
}
