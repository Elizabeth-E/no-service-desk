package nl.inholland.student.noservicedesk.database;

import org.bson.Document;

public class MongoHelper {
    // Helper to unwrap Mongo's date object
    public static String getMongoDateString(Object value) {
        if (value instanceof Document d) {
            return d.getString("$date");
        }
        return value != null ? value.toString() : null;
    }

    // Helper to unwrap Mongo's ObjectId
    public static String getMongoObjectIdString(Object value) {
        if (value instanceof Document d) {
            return d.getString("$oid");
        }
        return value != null ? value.toString() : null;
    }
}
