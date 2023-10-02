package gitlet;



import java.io.File;
import static gitlet.Utils.*;

import java.io.Serializable;
import java.util.*;

/** Represents a gitlet repository.
 *  Holds all the functions of the repository.
 *
 *  @author RENE AREVALO
 */
public class Repository {
    /**
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
    public static final File ADD_DIR = join(CWD, ".gitlet", "staging", "addStage");
    public static final File RM_DIR = join(CWD, ".gitlet", "staging", "rmStage");
    /** The ref directory. Will hold the head and master refs */
    public static final File REF_DIR_MASTER = join(CWD, ".gitlet", "refs", "head", "master");
    /** The object directory. Will hold the commit and blob objects. */
    public static final File OBJECT_DIR = join(CWD, ".gitlet", "objects");
    public static final File COMMIT_DIR = join(CWD, ".gitlet", "objects", "commits");
    public static final File BLOB_DIR = join(CWD, ".gitlet", "objects", "blobs");
    public static final File HEAD_DIR = join(CWD, ".gitlet", "HEAD");
    /**
     Treat the blobMap as the staging area. Hashmap of the blobs. {Key = SHA-1 id, Value = Filename}
     */
    private static  HashMap<String, String> blobMap = new HashMap<>();
    private static ArrayList<String> rmList = new ArrayList<>();
    private static String activeBranch = "master";
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
        initialCommit.setHead();
        initialCommit.setBranch();
        saveBlobMap();
        saveRemoveStage(rmList);

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
        blobMap = Helper.fromFileBlobMap();
        rmList = Helper.fromFileRmList();
        rmList.remove(filename);
        saveRemoveStage(rmList);

        if (Helper.blobIsDifferent(addBlob.getID())) {
            //add current blob to staging hashmap
            blobMap.put(addBlob.getID(), addBlob.getFilename());
            //write current blob object to disk
            File blobFile = join(BLOB_DIR, addBlob.getID());
            writeObject(blobFile, addBlob);
            saveBlobMap();
        }
    }

    @SuppressWarnings("unchecked")
    public static void rm(String filename) {
        rmList = Helper.fromFileRmList();
        blobMap = Helper.fromFileBlobMap();
        File filenameCheck = join(CWD, filename);
        if (blobMap.containsValue(filename)) {
            blobMap.remove(Helper.getKeyFromValue(blobMap, filename));
            saveBlobMap();
        } else if (Helper.fileTrackedByCurrentCommit(filename)) {
            if (filenameCheck.isFile()) {
                rmList.add(filename);
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
        blobMap = Helper.fromFileBlobMap();
        return new HashMap<>(blobMap);
    }
    /**
     * Clears the staging area by clearing both the addstage and remove stage.
     * */
    @SuppressWarnings("unchecked")
    public static void clearStaging() {
        blobMap = Helper.fromFileBlobMap();
        rmList = Helper.fromFileRmList();
        blobMap.clear();
        rmList.clear();
        saveBlobMap();
        saveRemoveStage(rmList);

    }
    /**
     * Testing method to check what is currently in the add stage.
     * */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> readAddStage() {
        HashMap<String, String> map;
        map = Utils.readObject(ADD_DIR, HashMap.class);
        ArrayList<String> addList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            addList.add(entry.getValue());
        }
        return addList;
    }
    /**
     * Testing method to check what is currently in the remove stage.
     * */
    @SuppressWarnings("unchecked")
    public static void readRemoveStage() {
        ArrayList<String> list;
        list = Utils.readObject(RM_DIR, ArrayList.class);
        System.out.println("Files that are staged for removal : " + list);
    }


    /**
     * Method to save the blobMap to disk
     * */
    private static void saveBlobMap()  {
        File blobHashMap = join(STAGING_DIR, "addStage");
        writeObject(blobHashMap, Repository.blobMap);
    }
    /**
     * Method to save the RemoveStage to disk
     * */
    private static void saveRemoveStage(List<String> removeList) {
        File removeListDir = join(STAGING_DIR, "removeStage");
        writeObject(removeListDir, (Serializable) removeList);
    }
    /**
     * Getter method for the RMList*/
    public static ArrayList<String> getRmList() {
        return Helper.fromFileRmList();
    }
    public static void checkout(String[] args)  {
        int length = args.length;
        final int lengthOfFirstCase = 3;
        final int lengthOfSecondCase = 4;
        final int lengthOfThirdCase = 2;
        if (length == lengthOfFirstCase) {
            String filenameCase1 = args[2];
            String headPointer = Utils.readContentsAsString(HEAD_DIR);
            checkoutHelper(headPointer, filenameCase1);
        } else if (length == lengthOfSecondCase) {
            String commitPointer = args[1];
            String filenameCase2 = args[3];
            checkoutHelper(commitPointer, filenameCase2);
        } else if (length == lengthOfThirdCase) {
            String branchName = args[1];
            File branch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);
            List<String> filesInWorkingDirectory = Utils.plainFilenamesIn(CWD);
            if (!branch.exists()) {
                System.out.println("No such branch exists.");
            } else if (Helper.getActiveBranch().equals(branchName)) {
                System.out.println("No need to checkout the current branch.");
            } else {
                assert filesInWorkingDirectory != null;
                for (String file : filesInWorkingDirectory) {
                    if (!Helper.fileTrackedByCurrentCommit(file)
                            && Helper.fileTrackedByCommitBranch(file, branchName)) {
                        System.out.println("There is an untracked file in the way; delete it, "
                                + "or add and commit it first.");
                        System.exit(0);
                    }
                    if (Helper.fileTrackedByCurrentCommit(file)
                            && !Helper.fileTrackedByCommitBranch(file, branchName)) {
                        Utils.restrictedDelete(file);
                    }
                }
                checkoutBranchHelper(branchName);
                Helper.setActiveBranch(branchName);
                Helper.switchHEAD();
                clearStaging();
            }
        }
    }
    private static void checkoutHelper(String commitPointer, String filename) {
        List<String> commitList = Utils.plainFilenamesIn(COMMIT_DIR);
        assert commitList != null;
        if (!commitList.contains(commitPointer)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c = Commit.fromFileCommit(commitPointer);
        if (c.getBlobMap().containsValue(filename)) {
            String blobID = Helper.getKeyFromValue(c.getBlobMap(), filename);
            Helper.readWriteBlobFromCommit(blobID, filename);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    private static void checkoutBranchHelper(String branchName) {
        File branch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);

        Commit c = Commit.fromFileCommit(Utils.readContentsAsString(branch));

        for (Map.Entry<String, String> entry : c.getBlobMap().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            File file = Utils.join(CWD, value);
            if (file.exists()) {
                Helper.readWriteBlobFromCommit(key, value);
            } else {
                Helper.writeNewFileFromBlob(key, value);
            }
        }
    }
    public static void branch(String branchName)  {
        File newBranch = Utils.join(CWD, ".gitlet", "refs", "head", branchName);
        if (newBranch.isFile()) {
            System.out.println("A branch with that name already exists.");
        } else {
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
    public static void status() {
        ArrayList<String> removeList = Helper.fromFileRmList();
        ArrayList<String> addList = readAddStage();
        Collections.sort(removeList);
        Collections.sort(addList);
        String currentActiveBranch = Helper.getActiveBranch();
        File headFiles = Utils.join(CWD, ".gitlet", "refs", "head");

        System.out.println("=== Branches ===");
        System.out.println("*" + currentActiveBranch);
        for (String branch : Objects.requireNonNull(headFiles.list())) {
            if (branch.equals(currentActiveBranch)) {
                continue;
            } else {
                System.out.println(branch);
            }
        }

        System.out.printf("%n");
        System.out.println("=== Staged Files ===");
        for (String add : addList) {
            System.out.println(add);
        }

        System.out.printf("%n");
        System.out.println("=== Removed Files ===");
        for (String rm : removeList) {
            System.out.println(rm);
        }

        System.out.printf("%n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.printf("%n");
        System.out.println("=== Untracked Files ===");
        System.out.printf("%n");
    }
    public static void reset(String commitID) {
        Commit c = Commit.fromFileCommit(commitID);
        HashMap<String, String> map = c.getBlobMap();
        List<String> filesInWorkingDirectory = Utils.plainFilenamesIn(CWD);

        assert filesInWorkingDirectory != null;
        for (String file : filesInWorkingDirectory) {
            if (!Helper.fileTrackedByCurrentCommit(file) && Helper.fileTrackedByCommitOnly(file, commitID)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            checkoutHelper(commitID, entry.getValue());
        }
        Commit.setHead(commitID);
        clearStaging();
    }
    public static void checkStagingAreas() {
        blobMap = Helper.fromFileBlobMap();
        rmList = Helper.fromFileRmList();
        if (!blobMap.isEmpty() || !rmList.isEmpty()) {
            System.out.println("You hava uncommitted changes.");
            System.exit(0);
        }
    }

}
