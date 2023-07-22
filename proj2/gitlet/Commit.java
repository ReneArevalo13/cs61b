package gitlet;

// TODO: any imports you need here




import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public String getParent(){return this.parent;};
    public String getId(){return this.id;};


    /**
     * Commit constructor. Takes in commit message as input. Sets the current commit time.
     * Sets the parent of this commit, which is the previous HEAD pointer. Tracks the blobs that are
     * being committed. Generates the SHA-1 id that is associated with the tracked information.
     * */
    public Commit(String message) {
        //metadata that must be tracked by SHA-1 ID
        this.message = message;
        this.timestamp = getTime();
        this.parent = setParent();
        this.blobsTracked = trackBlobs();
        //generate sha id
        this.id = generateSHA();
    }
    /**
     * Specific commit constructor that is used for the first Commit, init. Sets the required fields to
     * values that are specific to the init command requirements.*/
    public Commit(Integer initialize) {
        if (initialize == 0) {
            this.message = "initial commit";
            this.timestamp = getTimeEpoch();
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
        File previousHeadFilePath = Utils.join(Repository.HEAD_DIR);
        previousHeadFilePath.delete();
        //set new HEAD pointer file
        File headFilePath = Utils.join(Repository.HEAD_DIR);
        Utils.writeContents(headFilePath, this.id);
    }
    /**
     * Method to set the master branch pointer. It is the same as HEAD until a new branch is created.
     * */
    private void setMaster() {
        File previousHeadFilePath = Utils.join(Repository.REF_DIR);
        previousHeadFilePath.delete();
        File headFilePath = Utils.join(Repository.REF_DIR);
        Utils.writeContents(headFilePath, this.id);
    }
    public HashMap<String, String> getBlobMap() {
        return this.blobsTracked;
    }
    /**
     * Method that processes all the commands that must be done in order to commit a file into the repo.
     * Checks to make sure a blob is staged for addition.Creates the commit object. Moves the head pointer
     * to this latest commit object. Writes the commit object to disk. Lastly it clears the staging area.
     * */
    public static void makeCommit (String Message) {
        /*If no files have been staged, abort. Print the message No changes added to the commit.*/
        //TODO: what to do if no files staged.


        //construct the commit object
        Commit c = new Commit(Message);
        //set the head pointer as the most current commit id
        c.setHead();
        //set the master pointer
        c.setMaster();
        //save commit object to disk
        c.saveCommit();
        //clear the staging area
        Repository.clearStaging();

    }
    /**
     * Method to save the commit object to disk.
     * */
    public void saveCommit()  {
        Commit c = this;
        File commitFile = Utils.join(Repository.OBJECT_DIR, this.id);
        Utils.writeObject(commitFile, c);
    }
    /**
     * Method to generate teh SHA hash for the commit. Tracks the necessary fields including: message,
     *timestamp, parentID, and the blobs/files that the commit is tracking.
    * */
    private String generateSHA() {
        return Utils.sha1(this.message, this.timestamp, this.parent, this.blobsTracked.toString());
    }
    /**
     * Method to read in the Commit object from disk.
     * */
    public static Commit fromFileCommit(String CommitID) {
        File CommitFile = Utils.join(Repository.OBJECT_DIR, CommitID);
        return Utils.readObject(CommitFile, Commit.class);
    }
    /**
     * Method to set the parent of the newest commit. This is done by checking which commit the HEAD
     * pointer is referencing and using that as the parent to the commit.
     * */
    private String setParent() {
        File headFilePath = Utils.join(Repository.HEAD_DIR);
        return Utils.readContentsAsString(headFilePath);
    }
    /**
     * Method that updates the blobs that the commit is tracking. Goes through the addstage and rm stage
     * and changes the blobs tracked accordingly.
     * */
    private HashMap<String, String> trackBlobs() {
        //bring in parent BlobMap as map
        String headPointer = Utils.readContentsAsString(Repository.HEAD_DIR);
        Commit c = fromFileCommit(headPointer);
        HashMap<String, String> map= c.getBlobMap();
        //bring in the addstage hashmap
        HashMap<String, String> addMap= Repository.copyBlobMap();
        //update map to refelct the added files, assumed that all these should be the updates to commit
        for (Map.Entry<String,String> mapElement : addMap.entrySet()) {
            String value = mapElement.getValue();
            String keyToRemove = Repository.getKeyFromValue(map, value);
            map.remove(keyToRemove);
            map.put(mapElement.getKey(), mapElement.getValue());
        }
        //update map to reflect the files staged for removal.
        ArrayList<String > removeList = Repository.getRmList();
        for (String file : removeList) {
            String keyToRemove = Repository.getKeyFromValue(map, file);
            map.remove(keyToRemove);
        }
        return map;
    }
    /**
     * Test method to see that blobs a specific commit is tracking.
     * */
    public static void readBlobsTracked(String CommitID) {
        Commit c = fromFileCommit(CommitID);
        HashMap<String, String> map= c.getBlobMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    /**
     * Method to recursively go through the commit tree and extract the id, timestamp,
     * and message required for the log command. Starts at the most current commit, HEAD,
     * and goes until the init commit.
     * */
    private static ArrayList<LogBlob> goThroughParents(String commitPointer, ArrayList<LogBlob> logList) {
        //read in the commit
        Commit c = fromFileCommit(commitPointer);
//        ArrayList<LogBlob> logList = new ArrayList<>();
        //base case when the commit is the initial commit
        if (c.getParent().equals("null")) {
            //place contents into LogBlob for
            LogBlob L = new LogBlob(c.getId(), c.getTimestamp(), c.getMessage());
            logList.add(L);
            return logList;
        } else {
            LogBlob L = new LogBlob(c.getId(), c.getTimestamp(), c.getMessage());
            logList.add(L);
            //recursively go through the parents
            goThroughParents(c.getParent(), logList);
        }
        return logList;
    }
    public static void log() {
        String headPointer = Utils.readContentsAsString(Repository.HEAD_DIR);
        ArrayList<LogBlob> logList = new ArrayList<>();
        ArrayList<LogBlob> commitLog = goThroughParents(headPointer, logList);

        for (LogBlob k : commitLog) {
            System.out.println("===");
            System.out.println("commit " + k.getId());
            System.out.println("Date: " + k.getTimestamp());
            System.out.println(k.getMessage());
            System.out.printf("%n");
        }

    }
    public static String getTime() {
        return DateTimeFormatter.RFC_1123_DATE_TIME
               .withZone(ZoneId.systemDefault())
               .format(Instant.now());
    }
    public static String getTimeEpoch() {
        return DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneId.systemDefault())
                .format(Instant.EPOCH);
    }


}
