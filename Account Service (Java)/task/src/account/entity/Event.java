package account.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

import java.time.LocalDate;

@JsonPropertyOrder({
        "id",
        "date",
        "action",
        "subject",
        "object",
        "path"
})
@Entity
public class Event {
    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Basic
    @JsonProperty("date")
    private java.time.LocalDate date;
    @JsonProperty("action")
    private String action;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("object")
    private String object;
    @JsonProperty("path")
    private String path;

    public Event(){};

    public Event(String action, String subject, String object, String path) {
        this.date = LocalDate.now();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
