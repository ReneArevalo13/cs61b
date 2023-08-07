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
        Helper.setActiveBranch("master");
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
        BlobMap = Helper.fromFileBlobMap();
        if (Helper.blobIsDifferent(addBlob.getID())) {
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
        rmList = Helper.fromFileRmList();
        BlobMap = Helper.fromFileBlobMap();
        File filenameCheck = join(CWD, filename);
        //unstage the file if it is currently staged for addition
        //check to see if file is currently in staging area
        if (BlobMap.containsValue(filename)) {
            //remove this entry from Blobmap and consequently addList.
            BlobMap.remove(Helper.getKeyFromValue(BlobMap, filename));
//            addList.remove(String.valueOf(Filename));
            saveBlobMap(BlobMap);
        } else if (Helper.fileTrackedByCurrentCommit(filename)) {
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
        BlobMap = Helper.fromFileBlobMap();
        return new HashMap<>(BlobMap);
    }
    /**
     * Clears the staging area by clearing both the addstage and remove stage.
     * */
    @SuppressWarnings("unchecked")
    public static void clearStaging() {
        BlobMap = Helper.fromFileBlobMap();
        rmList = Helper.fromFileRmList();
        BlobMap.clear();
        rmList.clear();
        saveBlobMap(BlobMap);
        saveRemoveStage(rmList);

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
        return Helper.fromFileRmList();
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
            }else if (Helper.getActiveBranch().equals(branchName)) {
                System.out.println("No need to checkout the current branch.");
            } else {

                assert filesInWorkingDirectory != null;
                for (String file : filesInWorkingDirectory) {
//                    System.out.println(file + " is tracked: " + fileTrackedByCurrentCommit(file).toString());
                    if (!Helper.fileTrackedByCurrentCommit(file) && Helper.fileTrackedByCommit(file, branchName)) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                    if (Helper.fileTrackedByCurrentCommit(file) && !Helper.fileTrackedByCommit(file, branchName)) {
//                        System.out.println("second case");
                        Utils.restrictedDelete(file);
                    }
                }
                checkoutBranchHelper(branchName);
                Helper.setActiveBranch(branchName);
                Helper.switchHEAD();
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
            String blobID = Helper.getKeyFromValue(c.getBlobMap(), filename);
            Helper.readWriteBlobFromCommit(blobID, filename);
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
                Helper.readWriteBlobFromCommit(key, value);
            } else {
                Helper.writeNewFileFromBlob(key, value);
            }
        }
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
    public static void removeBranch(String branchName) {
        File branch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);
        if (!branch.isFile()) {
            System.out.println("A branch with that name does not exist.");
        } else if (Helper.getActiveBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch");
        } else {
            branch.delete();
        }

    }

}
