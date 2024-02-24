package account.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "email",
        "status"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomSuccessMessage {
    @JsonProperty("status")
    private String status;
    @JsonProperty("email")
    private String email;
    @JsonProperty("user")
    private String user;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
