package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static gitlet.Utils.join;

public class Helper {
    public static String getActiveBranch() {
        File currentBranch = Utils.join(Repository.GITLET_DIR, "refs", "currentBranch");
        return Utils.readContentsAsString(currentBranch);
    }
    public static void switchHEAD() {
        File previousHeadFilePath = Utils.join(Repository.HEAD_DIR);
        previousHeadFilePath.delete();
        File switchHead = Utils.join(Repository.HEAD_DIR);
        File newHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", getActiveBranch());
        String newHeadPointer = Utils.readContentsAsString(newHead);
        Utils.writeContents(switchHead, newHeadPointer);
    }
    public static void setActiveBranch(String branchName) {
        File currentBranch = Utils.join(Repository.GITLET_DIR, "refs", "currentBranch");
        currentBranch.delete();
        File setBranch = Utils.join(Repository.GITLET_DIR, "refs", "currentBranch");
        Utils.writeContents(setBranch, branchName);
    }
    public static void writeNewFileFromBlob(String blobID, String filename)  {
        File blobToRestore = Utils.join(Repository.BLOB_DIR, blobID);
        Blob readInBlob = Utils.readObject(blobToRestore, Blob.class);
        byte[] fileData = readInBlob.getContents();
        File fileOfInterest = Utils.join(Repository.CWD, filename);
//        fileOfInterest.createNewFile();
        Utils.writeContents(fileOfInterest, fileData);
    }
    public static void readWriteBlobFromCommit(String blobID, String filename) {
//        String blobID = getKeyFromValue(map, filename);
        File blobToRestore = Utils.join(Repository.BLOB_DIR, blobID);
        Blob readInBlob = Utils.readObject(blobToRestore, Blob.class);
        byte[] fileData = readInBlob.getContents();
        File fileOfInterest = Utils.join(Repository.CWD, filename);
        Utils.writeContents(fileOfInterest, fileData);
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
    public static Boolean fileTrackedByCommitBranch(String filename, String branchName) {
        //get the SHAid from the HEAD commit
        File newBranch = Utils.join(Repository.CWD, ".gitlet", "refs", "head", branchName);
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
    public static Boolean fileTrackedByCommitOnly(String filename, String commitID) {
        //read in the most current commit
        Commit c = Commit.fromFileCommit(commitID);
        //check the previous commits blobMap and see if the file of interest is already tracked
        if (c.getBlobMap().containsValue(null)) {
            return true;
        } else {
            return c.getBlobMap().containsValue(filename);
        }
    }


    /**
     * Method to check whether the file is already tracked by the current commit.
     * Return True if this file is tracked by the most current commit.
     * Return False otherwise.
     * */
    public static Boolean fileTrackedByCurrentCommit(String filename) {
        //get the SHAid from the HEAD commit
        String headPointer = Utils.readContentsAsString(Repository.HEAD_DIR);
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
     * Method to check whether the blob to be added is not already tracked by the current commit.
     * Return True if this is a new blob.
     * Return False if this is the same blob that is already being tracked.
     * */
    public static Boolean blobIsDifferent(String blobID) {
        //get the SHAid from the HEAD commit
        String headPointer = Utils.readContentsAsString(Repository.HEAD_DIR);
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
     * Method to read in the BlobMap (addstage) from disk.
     * */
    @SuppressWarnings("unchecked")
    public static HashMap<String,String> fromFileBlobMap() {
        File blobHashMap = join(Repository.STAGING_DIR, "addStage");
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
    public static ArrayList<String> fromFileRmList() {
        File RmListDir = join(Repository.STAGING_DIR, "removeStage");
        if (RmListDir.isFile()) {
            return Utils.readObject(RmListDir, ArrayList.class);
        } else {
            return new ArrayList<String>();
        }
    }

}
