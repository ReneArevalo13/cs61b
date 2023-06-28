package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.join;

/** Represents a gitlet Blob.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  Blobs are the type of object in gitlet that track the version of the file.
 *  They are identified by their SHA-1 id
 *  Hold the saved contents of a file
 *
 *  @author RENE AREVALO
 */


public class Blob implements Serializable {
    private String id;
    private byte[] contents;
    private String filename;
//    private String filepath;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File STAGING_DIR = join(CWD, ".gitlet", "staging");
    /** The .gitlet directory. */
    /**
     Hashmap of the blobs. {Key = SHA-1 id, Value = Filename}
     */
//    private Map<String, String> BlobMap = new HashMap<>();

    /**
     * ArrayList of the blobIDs currently in the staging area. Will be used to know what blobs are currently
     * staged for addition and thus what blobs are to be tracked by a commit.
     */
    public static ArrayList<String> BlobList = new ArrayList<String>();




    /**
     Blob constructor that will create the blob and map SHA hash and filename to a hashmap.
     */
    public Blob(String filename) {
        this.filename = filename;
        File filepath = join(CWD, this.filename);
        this.contents = Utils.readContents(filepath);
        this.id = Utils.sha1(this.contents);
    }
    public String getID() {
        return this.id;
    }
    public String getFilename() {
        return this.filename;
    }

    /**
     Generate the SHA-1 hash for a blob of a given file.
     */
    public String createSHABlob(String filename) {
        File filepath = join(CWD, filename);
        return Utils.sha1(Utils.readContents(filepath));
    }
    // reads in the file of interest, and returns the byte string of the file at the instance of 'adding'
//    public byte[] readFile (String filename) {
//        File filepath = join(CWD, filename);
//        return Utils.readContents(filepath);
//    }

    /**
     * Check the BlobMap via the key-value operations to make sure that the blob of interest isn't the same
     * as what is being added. This is done by comparing SHA codes as we expect that there are all unique hash codes.
     * Return True if there is already a blob of the same hash code, false otherwise.*/
    /*public boolean blobCheck (String blobID) {
        *//*use hashmap.containsKey to check if key exists in hashmap
        read in the most current commit (known by the HEAD pointer)*//*
        File headPath = Utils.join(Repository.REF_DIR, "head", "master");
        String head = Utils.readContentsAsString(headPath);
        Commit c = Commit.fromFile(head);
        HashMap<String, String> h = c.getBlobMap();


    }*/







}
