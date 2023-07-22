package gitlet;

import java.io.Serializable;

public class LogBlob implements Serializable {
    private String id;
    private String timestamp;
    private String message;

    public LogBlob(String id, String timestamp, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.message = message;
    }
    public String getTimestamp() {
        return this.timestamp;
    }
    public String getMessage() {
        return this.message;
    }
    public String getId() {
        return this.id;
    }
}
