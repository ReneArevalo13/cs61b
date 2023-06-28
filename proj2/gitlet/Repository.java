package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
    /**
     Treat the BlobMap as the staging area. Hashmap of the blobs. {Key = SHA-1 id, Value = Filename}
     */
    private static HashMap<String, String> BlobMap = new HashMap<>();

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




//        initialCommit.makeEpoch();
//        initialCommit.firstParent();
//        initialCommit.firstBlob();

//        File initialCommitFile = join(GITLET_DIR, "initialCommit");
//        writeObject(initialCommitFile, initialCommit);

    }

    public static void add(String filename) {
        File filenameCheck = join(CWD, filename);

        if (!filenameCheck.isFile()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        //construct blob object of the given file
        Blob addBlob = new Blob(filename);
/*
        verify that this blob is new and not already tracked
*/
        /*if () {
            *//*Remove file from staging area.
            * Make sure that the commit tracks the blob
            * just make sure that it isn't creating a new object
            * on disk*//*
        }*/
        //add current blob to the blob hash map
        BlobMap.put(addBlob.getID(), addBlob.getFilename());
        //write current blob object to disk
        File blobFile = join(OBJECT_DIR, addBlob.getID());
        writeObject(blobFile, addBlob);
        //write the blob hashmap to disk to maintain persistence
        File blobHashMap = join(STAGING_DIR, "addstage");
        writeObject(blobHashMap, BlobMap);
    }
    public static HashMap<String, String> copyBlobMap() {
        return new HashMap<>(BlobMap);
    }
    /**
     * Clears the staging area by removing the file that holds what blobs are going to be staged
     * */
    public static void clearStaging() {
        File addstage = join(STAGING_DIR, "addstage");
        Utils.restrictedDelete(addstage);
    }
   /* public static String getHEAD() {
        String HEAD  = Utils.readContentsAsString(REF_DIR, "")
    }*/


}
