package gitlet;



import java.io.File;
import static gitlet.Utils.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author RENE AREVALO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The staging directory. Will hold all the blobs staged for a commit. */
    public static final File STAGING_DIR = join(CWD, ".gitlet", "staging");
    /** The ref directory. Will hold the head and master refs */
    public static final File REF_DIR = join(CWD, ".gitlet", "refs", "head", "master");
    /** The object directory. Will hold the commit and blob objects. */
    public static final File OBJECT_DIR = join(CWD, ".gitlet", "objects");
    public static final File HEAD_DIR = join(CWD, ".gitlet", "HEAD");
    /**
     Treat the BlobMap as the staging area. Hashmap of the blobs. {Key = SHA-1 id, Value = Filename}
     */
    private static  HashMap<String, String> BlobMap = new HashMap<>();

    /* TODO: fill in the rest of this class. */
    /**
     * TODO: make branch pointer point to MASTER and HEAD
     * TODO: add SHA id
     * TODO: create other directories that will be used inside of gitlet
     * TODO: creat the rest of the infrastructure for

     * Creates a new Gitlet version-control system in the current directory. This system will automatically start with
     * one commit: a commit that contains no files and has the commit message initial commit (just like that, with
     * no punctuation). It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1
     * January 1970 in whatever format you choose for dates (this is called “The (Unix) Epoch”, represented internally
     * by the time 0.) Since the initial commit in all repositories created by Gitlet will have exactly the
     * same content, it follows that all repositories will automatically share this commit (they will all have the same
     * UID) and all commits in all repositories will trace back to it.
     */
    public static void init() {

        if (GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        REF_DIR.mkdirs();
        OBJECT_DIR.mkdir();
        Commit initialCommit = new Commit(0);

    }
    @SuppressWarnings("unchecked")
    public static void add(String filename) {
        File filenameCheck = join(CWD, filename);

        if (!filenameCheck.isFile()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        //construct blob object of the given file
        Blob addBlob = new Blob(filename);
        BlobMap = fromFile();
        //verify that this blob is NEW and not already tracked
        if (blobIsDifferent(addBlob.getID())) {

            //add current blob to staging hashmap
            BlobMap.put(addBlob.getID(), addBlob.getFilename());

            //write current blob object to disk
            File blobFile = join(OBJECT_DIR, addBlob.getID());
            writeObject(blobFile, addBlob);

            //write the blob hashmap to disk to maintain persistence
            File blobHashMap = join(STAGING_DIR, "addstage");
            // look at Utils.writecontents, might be better suited for what we're trying to do here.
            //writeContents(blobHashMap, BlobMap);
            writeObject(blobHashMap, BlobMap);

        } else {
            /*this is when the blob is tracked by the previous commit
            * and the file has NOT changed. We just add the blob id and
            * filename to the hashmap to know what this commit is tracking*/

            //add current blob to the blob hash map
            BlobMap.put(addBlob.getID(), addBlob.getFilename());
        }
        //add current blob to the blob hash map
//        BlobMap.put(addBlob.getID(), addBlob.getFilename());
//        //write current blob object to disk
//        File blobFile = join(OBJECT_DIR, addBlob.getID());
//        writeObject(blobFile, addBlob);
//        //write the blob hashmap (staging) to disk to maintain persistence
//        File blobHashMap = join(STAGING_DIR, "addstage");
//        writeObject(blobHashMap, BlobMap);
    }
    public static HashMap<String, String> copyBlobMap() {
        return new HashMap<>(BlobMap);
    }
    /**
     * Clears the staging area by removing the file that holds what blobs are going to be staged
     * */
    public static void clearStaging() {
        File addstage = join(STAGING_DIR, "addstage");
        addstage.delete();
        BlobMap.clear();
    }
    /**
     * Method to check whether the blob to be added is not already tracked by the current commit.
     * Return True if this is a new blob.
     * Return False if this is the same blob that is already being tracked.
     * */
    private static Boolean blobIsDifferent(String blobID) {
        //get the SHAid from the HEAD commit
        String headPointer = Utils.readContentsAsString(HEAD_DIR);

        //read in the most current commit
        Commit c = Commit.fromFile(headPointer);
        //check the commit's blobMap and see if the blob of interest is already there
        if (c.getBlobMap().containsKey(null)) {
            return true;
        } else {
            return !c.getBlobMap().containsKey(blobID);
        }
    }
    @SuppressWarnings("unchecked")
    public static void readAddstage() {
        File blobHashMap = join(STAGING_DIR, "addstage");
        HashMap<String, String> map;
        map = Utils.readObject(blobHashMap, HashMap.class);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }

    public static HashMap fromFile() {
        File blobHashMap = join(STAGING_DIR, "addstage");
        if (blobHashMap.isFile()) {
            return Utils.readObject(blobHashMap, HashMap.class);
        } else {
            return new HashMap<String, String>();
        }

    }

}
