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
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HandledTicket {
    @JsonProperty("_id")
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId _id;
    private Instant handledDate;
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId ticketId;
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId handledBy;
    private String comment;

    public ObjectId get_Id() {
        return _id;
    }

    public void set_Id(ObjectId id) {
        this._id = _id;
    }

    public Instant getHandledDate() {
        return handledDate;
    }

    public void setHandledDate(Instant handledDate) {
        this.handledDate = handledDate;
    }

    public ObjectId getTicketId() {
        return ticketId;
    }

    public void setTicketId(ObjectId ticketId) {
        this.ticketId = ticketId;
    }

    public ObjectId getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(ObjectId handledBy) {
        this.handledBy = handledBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
