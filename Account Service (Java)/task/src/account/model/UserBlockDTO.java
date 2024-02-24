package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserBlockDTO {
    @JsonProperty("user")
    private String email;
    @JsonProperty("operation")
    private String operation;

    public String getEmail() {
        return email;
    }

    public String getOperation() {
        return operation;
    }
}
