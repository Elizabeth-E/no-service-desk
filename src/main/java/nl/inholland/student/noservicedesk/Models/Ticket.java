package nl.inholland.student.noservicedesk.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nl.inholland.student.noservicedesk.database.ObjectIdDeserializer;
import nl.inholland.student.noservicedesk.database.ObjectIdSerializer;
import org.bson.types.ObjectId;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticket {
    @JsonProperty("_id")
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId _id;
    private Instant date_created;
    private Instant deadline;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private boolean is_resolved;
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId reported_by;
    private String reported_by_name;

    public Ticket() {
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId id) {
        this._id = id;
    }

    public Instant getDate_created() {
        return date_created;
    }

    public void setDate_created(Instant date_created) {
        this.date_created = date_created;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getIs_resolved() {
        return is_resolved;
    }

    public void setIs_resolved(boolean is_resolved) {
        this.is_resolved = is_resolved;
    }

    public ObjectId getReported_by() {
        return reported_by;
    }

    public void setReported_by(ObjectId reported_by) {
        this.reported_by = reported_by;
    }

    public String getReported_by_name() {
        return reported_by_name;
    }

    public void setReported_by_name(String reported_by_name) {
        this.reported_by_name = reported_by_name;
    }
}
