package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;

import static gitlet.Utils.join;
import static jdk.internal.org.jline.reader.impl.LineReaderImpl.CompletionType.List;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * message: the message given by user when a commit is made
     * timestamp: the time that the commit was made
     * parent: pointer to the parent commit of this commit
     * files: pointer to the files being tracked by the commit
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Instant timestamp;
    private String parent;
    private String id;
    private ArrayList<String> blobsTracked;


//    represents the current working directory of the user
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File STAGING_DIR = join(CWD, ".gitlet", "staging");
    private static final Instant EPOCH = Instant.EPOCH;
    private final Instant now = Instant.now();

    public String getMessage() {
        return this.message;
    }
    public Instant getTimestamp() {
        return this.timestamp;
    }

    /**
     * Helper method to allow for the initial commit to have the Epoch time.
     */
    public void makeEpoch() {
        this.timestamp = EPOCH;
    }
    /**
     * Helper method to allow for the initial commit to have parents set to NULL.
     */
    public void firstParent(){this.parent = null;}
    public void firstBlob(){this.blobsTracked = null;}


    public Commit(String message) {
        this.message = message;
        this.timestamp = now;
        this.parent = setParent();
        this.blobsTracked = Blob.BlobList;
    }

    public static void main(String[] args) {
        Commit firstCommit = new Commit("First commit");
//        firstCommit.makeEpoch();
        System.out.println(firstCommit.getMessage());
        System.out.println(firstCommit.getTimestamp());

    }

}
