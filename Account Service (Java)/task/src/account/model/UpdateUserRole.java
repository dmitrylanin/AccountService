package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRole {
    @NotNull
    @JsonProperty("user")
    String email;
    @NotNull
    String role;
    @NotNull
    String operation;

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }
}
