package gitlet;
import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;



/** Represents a gitlet commit object.
 * Holds the metadata of the commit object. Time, Message,
 * parent commit id, and its own unique sha-id.
 *  @author RENE AREVALO
 */
public class Commit implements Serializable {
    /**
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
    /** The timestamp of this Commit. */
    private String timestamp;
    /** The parent id of this Commit. */
    private String [] parent = new String[2];
    /** The unique SHA-1 id of this Commit. */
    private String id;
    /** The hashmap of the blobs tracked by this Commit. */
    private HashMap<String, String> blobsTracked;
    /** Empty hashmap for initialization. */
    private HashMap<String, String> emptyHashMap = new HashMap<>();

    /** The current working directory */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** Retrieve the message of this Commit. */
    public String getMessage() {
        return this.message;
    }
    /** Retrieve the timestamp of this Commit. */
    public String getTimestamp() {
        return this.timestamp;
    }
    /** Retrieve the parent of this Commit. */
    public String[] getParent() {
        return this.parent;
    }
    /** Retrieve the ID of this Commit. */
    public String getId() {
        return this.id;
    }
    /**
     * Commit constructor. Takes in commit message as input. Sets the current commit time.
     * Sets the parent of this commit, which is the previous HEAD pointer. Tracks the blobs that are
     * being committed. Generates the SHA-1 id that is associated with the tracked information.
     * */
    public Commit(String message) {
        //metadata that must be tracked by SHA-1 ID
        this.message = message;
        this.timestamp = getTime();
        this.parent[0] = setParent();
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
            this.parent[0] = "null";
            this.blobsTracked = emptyHashMap;
            this.id = generateSHA();
            setHead();
            saveCommit();
        } else {
            System.exit(0);
        }
    }
    public Commit(String message, String headSHA, String mergeSHA) {
        //metadata that must be tracked by SHA-1 ID
        this.message = message;
        this.timestamp = getTime();
        this.parent[0] = headSHA;
        this.parent[1] = mergeSHA;
        this.blobsTracked = trackBlobs();
        //generate sha id
        this.id = Utils.sha1(this.message, this.timestamp, this.parent[0], this.parent[1], this.blobsTracked.toString());
    }
    /**
    * Method to set the HEAD pointer to the most recent commit. Will be held in the refs/HEAD folder.
    * Should only have the most recent commit ID saved.
    * */
    public void setHead() {
        //delete previous HEAD pointer file
        File previousHeadFilePath = Utils.join(Repository.HEAD_DIR);
        previousHeadFilePath.delete();
        //set new HEAD pointer file
        File headFilePath = Utils.join(Repository.HEAD_DIR);
        Utils.writeContents(headFilePath, this.id);
    }
    public static void setHead(String commitID) {
        //delete previous HEAD pointer file
        File previousHeadFilePath = Utils.join(Repository.HEAD_DIR);
        previousHeadFilePath.delete();
        //set new HEAD pointer file
        File headFilePath = Utils.join(Repository.HEAD_DIR);
        Utils.writeContents(headFilePath, commitID);
    }

    /**
     * Method to set the master branch pointer. It is the same as HEAD until a new branch is created.
     * */
    public void setBranch() {
        if (Helper.getActiveBranch().equals("master")) {
            File previousHeadFilePath = Utils.join(Repository.REF_DIR_MASTER);
            previousHeadFilePath.delete();
            File headFilePath = Utils.join(Repository.REF_DIR_MASTER);
            Utils.writeContents(headFilePath, this.id);
        } else {
            File newBranch = Utils.join(CWD, ".gitlet", "refs", "head", Helper.getActiveBranch());
            newBranch.delete();
            File newBranchHead = Utils.join(CWD, ".gitlet", "refs", "head", Helper.getActiveBranch());
            Utils.writeContents(newBranchHead, this.id);
        }

    }
    public HashMap<String, String> getBlobMap() {
        return this.blobsTracked;
    }
    /**
     * Method that processes all the commands that must be done in order to commit a file into the repo.
     * Checks to make sure a blob is staged for addition.Creates the commit object. Moves the head pointer
     * to this latest commit object. Writes the commit object to disk. Lastly it clears the staging area.
     * */
    public static void makeCommit(String message) {
        Commit c = new Commit(message);
        if (c.message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        c.setHead();
        c.setBranch();
        c.saveCommit();
        Repository.clearStaging();
    }
    public static void makeCommit(String message, String headSHA, String mergeSHA) {
        Commit c = new Commit(message, headSHA, mergeSHA);
        c.setHead();
        c.setBranch();
        c.saveCommit();
        Repository.clearStaging();
    }
    /**
     * Method to save the commit object to disk.
     * */
    public void saveCommit()  {
        Commit c = this;
        File commitFile = Utils.join(Repository.COMMIT_DIR, this.id);
        Utils.writeObject(commitFile, c);
    }
    /**
     * Method to generate teh SHA hash for the commit. Tracks the necessary fields including: message,
     *timestamp, parentID, and the blobs/files that the commit is tracking.
    * */
    private String generateSHA() {
        return Utils.sha1(this.message, this.timestamp, this.parent[0], this.blobsTracked.toString());
    }
    /**
     * Method to read in the Commit object from disk.
     * */
    public static Commit fromFileCommit(String commitID) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        if (!commitFile.isFile()) {
            System.out.println("No commit with that id exists.");
        }
        return Utils.readObject(commitFile, Commit.class);
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
        HashMap<String, String> map = c.getBlobMap();
        //bring in the addstage hashmap
        HashMap<String, String> addMap = Repository.copyBlobMap();
        ArrayList<String> removeList = Repository.getRmList();

        if (addMap.isEmpty() && removeList.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //update map to reflect the added files, assumed that all these should be the updates to commit
        for (Map.Entry<String, String> mapElement : addMap.entrySet()) {
            String value = mapElement.getValue();
            String keyToRemove = Helper.getKeyFromValue(map, value);
            map.remove(keyToRemove);
            map.put(mapElement.getKey(), mapElement.getValue());
        }
        //update map to reflect the files staged for removal.
        for (String file : removeList) {
            String keyToRemove = Helper.getKeyFromValue(map, file);
            map.remove(keyToRemove);
        }
        return map;
    }
    /**
     * Test method to see that blobs a specific commit is tracking.
     * */
    public static void readBlobsTracked(String commitID) {
        Commit c = fromFileCommit(commitID);
        HashMap<String, String> map = c.getBlobMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    /**
     * Method to recursively go the rmList = Helper.fromFileRmList();rough the commit
     * tree and extract the id, timestamp and message required for the log command.
     * Starts at the most current commit, HEAD, and goes until the init commit.
     * */
    private static ArrayList<LogBlob> goThroughParents(String commitPointer, ArrayList<LogBlob> logList) {
        Commit c = fromFileCommit(commitPointer);

        if (c.getParent()[0].equals("null")) {
            LogBlob L = new LogBlob(c.getId(), c.getTimestamp(), c.getMessage());
            logList.add(L);
            return logList;
        } else {
            LogBlob L = new LogBlob(c.getId(), c.getTimestamp(), c.getMessage());
            logList.add(L);
            goThroughParents(c.getParent()[0], logList);
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
    public static void globalLog() {
        List<String> commitList = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        assert commitList != null;
        for (String file : commitList) {
            Commit c = fromFileCommit(file);
            System.out.println("===");
            System.out.println("commit " + c.getId());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.printf("%n");
        }
    }
    public static void find(String commitMessage) {
        List<String> commitList = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        assert commitList != null;
        boolean messageExists = false;
        for (String file : commitList) {
            Commit c = fromFileCommit(file);
            if (commitMessage.equals(c.message)) {
                System.out.println(c.getId());
                messageExists = true;
            }
        }
        if (!messageExists) {
            System.out.println("Found no commit with that message.");
        }
    }
    public static String getTime() {
        //closest time format to spec
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss yyyy -0800");
        return myDateObj.format(myFormatObj);
    }
    public static String getTimeEpoch() {
        LocalDateTime myDateObj = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss yyyy -0800");
        return myDateObj.format(myFormatObj);
    }


}
