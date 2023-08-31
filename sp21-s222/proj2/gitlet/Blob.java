package gitlet;


import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;

/** Represents a gitlet Blob.
 *  Blobs are the type of object in gitlet that track the version of the file.
 *  They are identified by their SHA-1 id
 *  Hold the saved contents of a file
 *
 *  @author RENE AREVALO
 */


public class Blob implements Serializable {
    private String id;
    private final String filename;
    private byte[] contents;
    public static final File CWD = new File(System.getProperty("user.dir"));


    /**
     Blob constructor that will create the blob and map SHA hash and filename to a hashmap.
     */
    public Blob(String filename) {
        this.filename = filename;
        File filepath = join(CWD, this.filename);
        this.contents = Utils.readContents(filepath);
        this.id = Utils.sha1(filename, contents);
    }
    public String getID() {
        return this.id;
    }
    public String getFilename() {
        return this.filename;
    }
    public byte[] getContents() {
        return this.contents;
    }

}
