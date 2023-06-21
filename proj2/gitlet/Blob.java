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
    private Map<String, String> BlobMap = new HashMap<>();

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
        File filepath = join(CWD, filename);
        this.contents = Utils.readContents(filepath);
        this.id = Utils.sha1(this.contents);
        BlobList.add(this.id);
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

    // checks all the files in the staging area, and is looking for file identical to the current working
    // version. If true return boolean true and will be added to other function. False if there are no matches.
    public boolean blobCheck (String blobID) {

        List<String> allFiles = Utils.plainFilenamesIn(STAGING_DIR);
        for (String file : allFiles) {
            if (blobID.equals(file)) {
                return true;
            }
        }
        return false;
    }







}
