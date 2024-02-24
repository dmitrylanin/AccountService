package account.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "timestamp",
        "status",
        "error",
        "message",
        "path"
})
public class CustomErrorMessage {
    @JsonProperty("status")
    private int statusCode;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("message")
    private String message;
    @JsonProperty("error")
    private String error;
    @JsonProperty("path")
    private String path;

    public CustomErrorMessage(int statusCode, String message, String path, String error){
        this.statusCode = statusCode;
        this.timestamp = LocalDate.now().toString();
        this.message = message;
        this.path = path;
        this.error = error;
    }
}
