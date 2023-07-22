package gitlet;



import java.io.File;
import static gitlet.Utils.*;

import java.io.Serializable;
import java.util.*;
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
    private static ArrayList<String> rmList = new ArrayList<>();

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
        BlobMap = fromFileBlobMap();
        if (blobIsDifferent(addBlob.getID())) {
            //add current blob to staging hashmap
            BlobMap.put(addBlob.getID(), addBlob.getFilename());
            //write current blob object to disk
            File blobFile = join(OBJECT_DIR, addBlob.getID());
            writeObject(blobFile, addBlob);
            saveBlobMap(BlobMap);
        }
    }

    @SuppressWarnings("unchecked")
    public static void rm(String filename) {
        rmList = fromFileRmList();
        BlobMap = fromFileBlobMap();
        File filenameCheck = join(CWD, filename);
        //unstage the file if it is currently staged for addition
        //check to see if file is currently in staging area
        if (BlobMap.containsValue(filename)) {
            //remove this entry from Blobmap and consequently addList.
            BlobMap.remove(getKeyFromValue(BlobMap, filename));
//            addList.remove(String.valueOf(Filename));
            saveBlobMap(BlobMap);
        } else if (fileTrackedByCommit(filename)) {
            if (filenameCheck.isFile()) {
                //add file to the rm stage
                rmList.add(filename);
                //delete file from working directory
                Utils.restrictedDelete(filenameCheck);
            } else {
                rmList.add(filename);
            }
        } else {
            System.out.println("No reason to remove the file.");
        }
        saveRemoveStage(rmList);

    }
    @SuppressWarnings("unchecked")
    public static HashMap<String, String> copyBlobMap() {
        BlobMap = fromFileBlobMap();
        return new HashMap<>(BlobMap);
    }
    /**
     * Clears the staging area by clearing both the addstage and remove stage.
     * */
    @SuppressWarnings("unchecked")
    public static void clearStaging() {
        BlobMap = fromFileBlobMap();
        rmList = fromFileRmList();
        BlobMap.clear();
        rmList.clear();
        saveBlobMap(BlobMap);
        saveRemoveStage(rmList);

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
        Commit c = Commit.fromFileCommit(headPointer);
        //check the previous commits blobMap and see if the blob of interest is already tracked
        if (c.getBlobMap().containsKey(null)) {
            return true;
        } else {
            return !c.getBlobMap().containsKey(blobID);
        }
    }
    /**
     * Method to check whether the file is already tracked by the current commit.
     * Return True if this file is tracked by the most current commit.
     * Return False otherwise.
     * */
    private static Boolean fileTrackedByCommit(String filename) {
        //get the SHAid from the HEAD commit
        String headPointer = Utils.readContentsAsString(HEAD_DIR);
        //read in the most current commit
        Commit c = Commit.fromFileCommit(headPointer);
        //check the previous commits blobMap and see if the file of interest is already tracked
        if (c.getBlobMap().containsValue(null)) {
            return true;
        } else {
            return c.getBlobMap().containsValue(filename);
        }
    }
    /**
     * Testing method to check what is currently in the add stage.
     * */
    @SuppressWarnings("unchecked")
    public static void readAddStage() {
        File blobHashMap = join(STAGING_DIR, "addStage");
        HashMap<String, String> map;
        map = Utils.readObject(blobHashMap, HashMap.class);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }
    /**
     * Testing method to check what is currently in the remove stage.
     * */
    @SuppressWarnings("unchecked")
    public static void readRemoveStage() {
        File removeListDir = join(STAGING_DIR, "removeStage");
        ArrayList<String > list;
        list = Utils.readObject(removeListDir,ArrayList.class);
        System.out.println("Files that are staged for removal : " + list);
    }
    /**
     * Method to read in the BlobMap (addstage) from disk.
     * */
    @SuppressWarnings("unchecked")
    private static HashMap<String,String> fromFileBlobMap() {
        File blobHashMap = join(STAGING_DIR, "addStage");
        if (blobHashMap.isFile()) {
            return Utils.readObject(blobHashMap, HashMap.class);
        } else {
            return new HashMap<String, String>();
        }
    }
    /**
     * Method to read in the remove stage list from disk.
     * */
    @SuppressWarnings("unchecked")
    private static ArrayList<String> fromFileRmList() {
        File RmListDir = join(STAGING_DIR, "removeStage");
        if (RmListDir.isFile()) {
            return Utils.readObject(RmListDir, ArrayList.class);
        } else {
            return new ArrayList<String>();
        }
    }
    /**
     * Method to get the key from a given value in a hashmap.
     * */
    public static String getKeyFromValue(Map<String, String> map, String  value) {
        String result = "";
        if (map.containsValue(value)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (Objects.equals(entry.getValue(), value)) {
                    result = entry.getKey();
                }
            }
        }
        return result;
    }
    private static void saveBlobMap(HashMap<String, String> BlobMap)  {
        File blobHashMap = join(STAGING_DIR, "addStage");
        writeObject(blobHashMap, Repository.BlobMap);
    }
    private static void saveRemoveStage(List<String> rmList) {
        File rmListDir = join(STAGING_DIR, "removeStage");
        writeObject(rmListDir, (Serializable) rmList);
    }
    public static ArrayList<String> getRmList() {
        return fromFileRmList();
    }


}
