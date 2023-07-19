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
//        addList = fromFileAddList();
        //verify that this blob is NEW and not already tracked
        if (blobIsDifferent(addBlob.getID())) {
//            System.out.println("THIS IS A NEW BLOB");

            //add current blob to staging hashmap
            BlobMap.put(addBlob.getID(), addBlob.getFilename());
            //track filename for logging what is in staging area
//            addList.add(addBlob.getFilename());

            //write current blob object to disk
            File blobFile = join(OBJECT_DIR, addBlob.getID());
            writeObject(blobFile, addBlob);
            saveBlobMap(BlobMap);

            //write the blob hashmap to disk to maintain persistence
//            File blobHashMap = join(STAGING_DIR, "addstage");
//            File addListDir = join(STAGING_DIR, "addlist");
//            writeObject(blobHashMap, BlobMap);
//            writeObject(addListDir, addList);
        }

//        else {
            /*this is when the blob is tracked by the previous commit
            * and the file has NOT changed. We just add the blob id and
            * filename to the hashmap to know what this commit is tracking*/

            //add current blob to the blob hash map
//            System.out.println("THIS BLOB IS NOT NEW");
//            BlobMap.put(addBlob.getID(), addBlob.getFilename());
        }
        //add current blob to the blob hash map
//        BlobMap.put(addBlob.getID(), addBlob.getFilename());
//        //write current blob object to disk
//        File blobFile = join(OBJECT_DIR, addBlob.getID());
//        writeObject(blobFile, addBlob);
//        //write the blob hashmap (staging) to disk to maintain persistence
//        File blobHashMap = join(STAGING_DIR, "addstage");
//        writeObject(blobHashMap, BlobMap);

    @SuppressWarnings("unchecked")
    public static void rm(String filename) {
        rmList = fromFileRmList();
        //unstage the file if it is currently staged for addition
        //check to see if file is currently in staging area
        untrackFileFromAddStage(filename);
        //File is tracked by current commit and exists in current directory
        File filenameCheck = join(CWD, filename);
        if (fileTrackedByCommit(filename)) {
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
     * Clears the staging area by removing the file that holds what blobs are going to be staged
     * */
    @SuppressWarnings("unchecked")
    public static void clearStaging() {
        BlobMap = fromFileBlobMap();
        rmList = fromFileRmList();
        BlobMap.clear();
        rmList.clear();
//        File blobHashMap = join(STAGING_DIR, "addstage");
//        File addListDir = join(STAGING_DIR, "addlist");
//        writeObject(blobHashMap, BlobMap);
//        writeObject(addListDir, addList);
        saveBlobMap(BlobMap);
        saveRemoveStage(rmList);
//        File blobHashMap = join(STAGING_DIR, "addstage");
//        File addListDir = join(STAGING_DIR, "addlist");
//        blobHashMap.delete();
//        addListDir.delete();
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
    @SuppressWarnings("unchecked")
    public static void readAddStage() {
        File blobHashMap = join(STAGING_DIR, "addStage");
//        File addListDir = join(STAGING_DIR, "addlist");
        HashMap<String, String> map;
//        ArrayList<String> list;
        map = Utils.readObject(blobHashMap, HashMap.class);
//        list = Utils.readObject(addListDir, ArrayList.class);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
//        System.out.println("Files that are staged: " + list);

    }
    @SuppressWarnings("unchecked")
    public static void readRemoveStage() {
        File removeListDir = join(STAGING_DIR, "removeStage");
        ArrayList<String > list;
        list = Utils.readObject(removeListDir,ArrayList.class);
        System.out.println("Files that are staged for removal : " + list);
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String,String> fromFileBlobMap() {
        File blobHashMap = join(STAGING_DIR, "addStage");
        if (blobHashMap.isFile()) {
            return Utils.readObject(blobHashMap, HashMap.class);
        } else {
            return new HashMap<String, String>();
        }
    }
    @SuppressWarnings("unchecked")
    private static ArrayList<String> fromFileRmList() {
        File RmListDir = join(STAGING_DIR, "removeStage");
        if (RmListDir.isFile()) {
            return Utils.readObject(RmListDir, ArrayList.class);
        } else {
            return new ArrayList<String>();
        }
    }
//    @SuppressWarnings("unchecked")
//    private static boolean checkIfFileInAddstage(String filename) {
//        addList = fromFileAddList();
//        for(String file : addList) {
//            if (filename.equals(file)){
//                return true;
//            }
//        }
//        return false;
//    }
    @SuppressWarnings("unchecked")
    private static void untrackFileFromAddStage(String Filename) {
        BlobMap = fromFileBlobMap();
//        addList = fromFileAddList();
        if (BlobMap.containsValue(Filename)) {
            //remove this entry from Blobmap and consequently addList.
            BlobMap.remove(getKeyFromValue(BlobMap, Filename));
//            addList.remove(String.valueOf(Filename));
            saveBlobMap(BlobMap);
            }
        }


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
//    private static void saveAddList() {
//        File addListDir = join(STAGING_DIR, "addlist");
//        writeObject(addListDir, addList);
//    }
    private static void saveRemoveStage(List<String> rmList) {
        File rmListDir = join(STAGING_DIR, "removeStage");
        writeObject(rmListDir, (Serializable) rmList);
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<String> getRmList() {
        return fromFileRmList();
    }


}
