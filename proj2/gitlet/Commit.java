package gitlet;

// TODO: any imports you need here



import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static gitlet.Utils.join;


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
    private String timestamp;
    private String parent;
    private String id;
    private HashMap<String, String> blobsTracked;
    private HashMap<String, String> emptyHashMap = new HashMap<>();
    private String pointerHEAD;
    /**
     HashMap that will map the parent child relationship of commits. {Key = SHA-1 id of current commit,
     Value = SHA-1 id of the parent commit}
     */
    private HashMap<String, String> parentChildMap = new HashMap<>();


//    represents the current working directory of the user
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File STAGING_DIR = join(CWD, ".gitlet", "staging");
    private static final Instant EPOCH = Instant.EPOCH;
    private final Instant now = Instant.now();

    public String getMessage() {
        return this.message;
    }
    public String getTimestamp() {
        return this.timestamp;
    }
    public HashMap<String, String> getBlobMap() {return this.blobsTracked;}

    /**
     * Helper method to allow for the initial commit to have the Epoch time.
     */
    public void makeEpoch() {
        this.timestamp = EPOCH.toString();
    }
    public void setFirstParent() {
        parentChildMap.put(this.id, "");
    }
    /**
     * Helper method to allow for the initial commit to have parents set to NULL.
     */
    public void firstParent(){this.parent = "null";}
    public void firstBlob(){this.blobsTracked = emptyHashMap;}

    /**
     * Commit constructor. Takes in commit message as input. Sets the the current commit time.
     * Sets the parent of this commit, which is the previous HEAD pointer. Tracks the blobs that are
     * being committed. Generates the SHA-1 id that is associated with the tracked information.
     * */
    public Commit(String message) {
        //metadata that must be tracked by SHA-1 ID
        this.message = message;
        this.timestamp = now.toString();
        this.parent = pointerHEAD;
        this.blobsTracked = Repository.copyBlobMap();
        //generate sha id
        this.id = generateSHA();
    }
    public Commit(Integer initialize) {
        if (initialize == 0) {
            this.message = "initial commit";
            this.timestamp = EPOCH.toString();
            this.parent = "null";
            this.blobsTracked = emptyHashMap;
            this.id = generateSHA();
            setHead();
            saveCommit();
        } else {
            System.exit(0);
        }
    }
    /**
    * Method to set the HEAD pointer to the most recent commit. Will be held in the refs/HEAD folder.
    * Should only have the most recent commit ID saved.
    * */
    private void setHead() {
        //delete previous HEAD pointer file
        File previousHeadFilePath = Utils.join(Repository.REF_DIR);
        //Utils.restrictedDelete(previousHeadFilePath);
        previousHeadFilePath.delete();

        //set new HEAD pointer file
        File headFilePath = Utils.join(Repository.REF_DIR);
        Utils.writeObject(headFilePath, this.id);
    }
    public static void makeCommit (String Message) {
        //construct the commit object
        Commit c = new Commit(Message);
        //set the head pointer as the most current commit id
        c.setHead();
        //save commit object to disk
        c.saveCommit();
        //clear the staging area
        Repository.clearStaging();

    }
    public void saveCommit()  {
        Commit c = this;
        File commitFile = Utils.join(Repository.OBJECT_DIR, this.id);
        Utils.writeObject(commitFile, c);
    }


    private String generateSHA() {
        return Utils.sha1(this.message, this.timestamp, this.parent, this.blobsTracked.toString());
    }

    public static Commit fromFile(String id) {
        // TODO (hint: look at the Utils file)
        File CommitFile = Utils.join(Repository.OBJECT_DIR, id);
        return Utils.readObject(CommitFile, Commit.class);
    }



}
