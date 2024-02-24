package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class ChangedPassword {
    @NotNull
    @JsonProperty("new_password")
    private String password;

    public String getPassword() {
        return password;
    }

}
