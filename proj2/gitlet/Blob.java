package gitlet;


import java.io.File;
import java.io.Serializable;

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
    private final String filename;
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     Blob constructor that will create the blob and map SHA hash and filename to a hashmap.
     */
    public Blob(String filename) {
        this.filename = filename;
        File filepath = join(CWD, this.filename);
        byte[] contents = Utils.readContents(filepath);
        this.id = Utils.sha1(contents);
    }
    public String getID() {
        return this.id;
    }
    public String getFilename() {
        return this.filename;
    }

}
