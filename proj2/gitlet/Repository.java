package gitlet;



import java.io.File;
import static gitlet.Utils.*;

import java.io.IOException;
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
    public static final File REF_DIR_MASTER = join(CWD, ".gitlet", "refs", "head", "master");
    /** The object directory. Will hold the commit and blob objects. */
    public static final File OBJECT_DIR = join(CWD, ".gitlet", "objects");
    public static final File COMMIT_DIR = join(CWD, ".gitlet", "objects", "commits");
    public static final File BLOB_DIR = join(CWD, ".gitlet", "objects", "blobs");


    public static final File HEAD_DIR = join(CWD, ".gitlet", "HEAD");
    /**
     Treat the BlobMap as the staging area. Hashmap of the blobs. {Key = SHA-1 id, Value = Filename}
     */
    private static  HashMap<String, String> BlobMap = new HashMap<>();
    private static ArrayList<String> rmList = new ArrayList<>();
    private static String activeBranch = "master";

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
        REF_DIR_MASTER.mkdirs();
        OBJECT_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        setActiveBranch("master");
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
            File blobFile = join(BLOB_DIR, addBlob.getID());
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
        } else if (fileTrackedByCurrentCommit(filename)) {
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
    private static Boolean fileTrackedByCurrentCommit(String filename) {
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
    private static Boolean fileTrackedByCommit(String filename, String branchName) {
        //get the SHAid from the HEAD commit
        File newBranch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);
        String pointer = Utils.readContentsAsString(newBranch);
        //read in the most current commit
        Commit c = Commit.fromFileCommit(pointer);
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
    /**
     * Method to save the BlobMap to disk
     * */
    private static void saveBlobMap(HashMap<String, String> BlobMap)  {
        File blobHashMap = join(STAGING_DIR, "addStage");
        writeObject(blobHashMap, Repository.BlobMap);
    }
    /**
     * Method to save the RemoveStage to disk
     * */
    private static void saveRemoveStage(List<String> rmList) {
        File rmListDir = join(STAGING_DIR, "removeStage");
        writeObject(rmListDir, (Serializable) rmList);
    }
    /**
     * Getter method for the RMList*/
    public static ArrayList<String> getRmList() {
        return fromFileRmList();
    }
    public static void checkout(String[] args) throws IOException {
        int length = args.length;
        if (length == 3) {
            String filenameCase1 = args[2];
            String headPointer = Utils.readContentsAsString(HEAD_DIR);
            checkoutHelper(headPointer, filenameCase1);
        } else if (length == 4) {
            String commitPointer = args[1];
            String filenameCase2 = args[3];
            checkoutHelper(commitPointer, filenameCase2);
        } else if (length == 2) {
//            System.out.println("active branch is " + getActiveBranch());
            String branchName = args[1];
            File branch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);
            List<String> filesInWorkingDirectory = Utils.plainFilenamesIn(CWD);
            if (!branch.exists()) {
                System.out.println("No such branch exists.");
            }else if (getActiveBranch().equals(branchName)) {
                System.out.println("No need to checkout the current branch.");
            } else {

                assert filesInWorkingDirectory != null;
                for (String file : filesInWorkingDirectory) {
//                    System.out.println(file + " is tracked: " + fileTrackedByCurrentCommit(file).toString());
                    if (!fileTrackedByCurrentCommit(file) && fileTrackedByCommit(file, branchName)) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                    if (fileTrackedByCurrentCommit(file) && !fileTrackedByCommit(file, branchName)) {
//                        System.out.println("second case");
                        Utils.restrictedDelete(file);
                    }
                }
                checkoutBranchHelper(branchName);
                setActiveBranch(branchName);
                switchHEAD();
                clearStaging();
            }
//            System.out.println("active branch is " + getActiveBranch());

        }
    }
    private static void checkoutHelper(String commitPointer, String filename) {
        List<String> commitList = Utils.plainFilenamesIn(COMMIT_DIR);
        Commit c = Commit.fromFileCommit(commitPointer);
        //check if commitID exists
        assert commitList != null;
        if (!commitList.contains(commitPointer)){
            System.out.println("No commit with that id exists.");
            return;
        }
        if (c.getBlobMap().containsValue(filename)){
            String blobID = getKeyFromValue(c.getBlobMap(), filename);
//            File blobToRestore = Utils.join(BLOB_DIR, blobID);
//            Blob readInBlob = Utils.readObject(blobToRestore, Blob.class);
//            byte[] fileData = readInBlob.getContents();
//            File fileOfInterest = Utils.join(CWD, filename);
//            Utils.writeContents(fileOfInterest, fileData);
            readWriteBlobFromCommit(blobID, filename);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }
    private static void checkoutBranchHelper(String branchName) throws IOException {
        File branch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);

        Commit c = Commit.fromFileCommit(Utils.readContentsAsString(branch));
        //go through all the files tracked by last commit and update the CWD files

        for (Map.Entry<String, String> entry : c.getBlobMap().entrySet()) {
            String key = entry.getKey();//shaID
            String value = entry.getValue();//filename
            //check if the file is in the working directory
            File file = Utils.join(CWD, value);
            if (file.exists()) {
                readWriteBlobFromCommit(key, value);
            } else {
                writeNewFileFromBlob(key, value);
            }
        }
    }
    private static void readWriteBlobFromCommit(String blobID, String filename) {
//        String blobID = getKeyFromValue(map, filename);
        File blobToRestore = Utils.join(BLOB_DIR, blobID);
        Blob readInBlob = Utils.readObject(blobToRestore, Blob.class);
        byte[] fileData = readInBlob.getContents();
        File fileOfInterest = Utils.join(CWD, filename);
        Utils.writeContents(fileOfInterest, fileData);
    }
    private static void writeNewFileFromBlob(String blobID, String filename) throws IOException {
        File blobToRestore = Utils.join(BLOB_DIR, blobID);
        Blob readInBlob = Utils.readObject(blobToRestore, Blob.class);
        byte[] fileData = readInBlob.getContents();
        File fileOfInterest = Utils.join(CWD, filename);
        fileOfInterest.createNewFile();
        Utils.writeContents(fileOfInterest, fileData);
    }
    public static void branch(String branchName) throws IOException {
        File newBranch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);
        if (newBranch.isFile()) {
            System.out.println("A branch with that name already exists.");
        } else {
            newBranch.createNewFile();
            //copy in the most current commit and write to new branch
            Utils.writeContents(newBranch, readContentsAsString(REF_DIR_MASTER));
        }
    }
    private static void setActiveBranch(String branchName) {
        File currentBranch = Utils.join(GITLET_DIR, "refs", "currentBranch");
        currentBranch.delete();
        File setBranch = Utils.join(GITLET_DIR, "refs", "currentBranch");
        Utils.writeContents(setBranch, branchName);
    }
    public static String getActiveBranch() {
        File currentBranch = Utils.join(GITLET_DIR, "refs", "currentBranch");
        return Utils.readContentsAsString(currentBranch);
    }
    private static void switchHEAD() {
        File previousHeadFilePath = Utils.join(HEAD_DIR);
        previousHeadFilePath.delete();
        File switchHead = Utils.join(HEAD_DIR);
        File newHead = join(CWD, ".gitlet", "refs", "head", getActiveBranch());
        String newHeadPointer = Utils.readContentsAsString(newHead);
        Utils.writeContents(switchHead, newHeadPointer);

    }



}
