package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
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
    private File filename;
//    private String filepath;
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private Map<String, String> BlobMap = new HashMap<>();



    // Blob constructor takes in the file name that it references (points to)
    public Blob(String filename) {
        this.id = createSHA(filename);
        BlobMap.put(this.id, filename);
    }
    public String getID() {
        return this.id;
    }

      /* Get the SHA-1 ID for the file that is to be referenced
      * */
    public String createSHA (String filename) {
        File filepath = join(CWD, filename);
        return Utils.sha1(Utils.readContents(filepath));
    }
    // reads in the file of interest, and returns the byte string of the file at the instance of 'adding'
    public byte[] readFile (String filename) {
        File filepath = join(CWD, filename);
        return Utils.readContents(filepath);
    }







}
