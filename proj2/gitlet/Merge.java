package gitlet;


import java.io.File;
import java.util.*;


public class Merge {
    public static Set<String>  filesSeen;
    public static boolean conflict = false;
    /**
     * Method to find the split point between the current working branch and the merging branch.
     * Return the SHAid of the split point commit.
     * */
    public static String splitPoint (String mergingBranch) {
        String minkey = null;
        Integer minvalue = 1000000;
        //bring in current HEAD commit from working branch and merging branch
        File currentHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", Helper.getActiveBranch());
        File mergingHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", mergingBranch);
        String [] currentBranchArray = new String[2];
        currentBranchArray[0] = "checkout";
        currentBranchArray[1] = Helper.getActiveBranch();
        String workingBranchPointer = Utils.readContentsAsString(currentHead);
        String mergingBranchPointer = Utils.readContentsAsString(mergingHead);
        //create commit maps to then compare against each other
        HashMap<String, Integer> workingMap = goThroughCommits(workingBranchPointer, new HashMap<>(), 0);
        HashMap<String, Integer> mergingMap = goThroughCommits(mergingBranchPointer, new HashMap<>(), 0);

        for (Map.Entry<String, Integer> entry1 : workingMap.entrySet()) {
            String key = entry1.getKey();
            Integer value = entry1.getValue();
            if (mergingMap.containsKey(key) && value < minvalue) {
                minkey = key;
                minvalue = entry1.getValue();
            }
        }
        //Check splitpoint with the other branches
        assert minkey != null;
        if (minkey.equals(mergingBranch)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else if (minkey.equals(currentHead)) {
            Repository.checkout(currentBranchArray);
            System.out.println("Current branch fast-forwarded.");
        }

        return minkey;
    }

    /**
     * Recursive method to iterate through the commits following the parent and get a total mapping of a "branch".
     * */
    private static HashMap<String, Integer> goThroughCommits(String commitPointer, HashMap<String, Integer> commitMap,
                                                             Integer counter) {
        Commit c = Commit.fromFileCommit(commitPointer);
        if (c.getParent()[0].equals("null")) {
            counter++;
            commitMap.put(c.getId(), counter);
            return commitMap;
        } else {
            commitMap.put(c.getId(), counter);
            counter++;
            goThroughCommits(c.getParent()[0], commitMap, counter);
        }
        return commitMap;
    }
    /**
     * Driver method to execute the Merge command from spec.*/
    public static void merge(String mergingBranch) {

        Repository.checkStagingAreas();
        File currentHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", Helper.getActiveBranch());
        File mergingHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", mergingBranch);

        if (!mergingHead.isFile()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (mergingBranch.equals(Helper.getActiveBranch())) {
            System.out.println("Cannot merge a branch with itself.");
        }

        String workingBranchPointer = Utils.readContentsAsString(currentHead);
        String mergingBranchPointer = Utils.readContentsAsString(mergingHead);
        String splitPointPointer = splitPoint(mergingBranch);
        checkFileConflicts(mergingBranchPointer);

        Commit splitPointCommit = Commit.fromFileCommit(splitPointPointer);
        Commit headCommit = Commit.fromFileCommit(workingBranchPointer);
        Commit mergingCommit = Commit.fromFileCommit(mergingBranchPointer);

        HashMap<String, String> headMap = headCommit.getBlobMap();
        HashMap<String, String> splitMap = splitPointCommit.getBlobMap();
        HashMap<String, String> mergingMap = mergingCommit.getBlobMap();

        Set<String> uniqueValues = new HashSet<>();
        Set<String>  filesSeen = filesInCommits(headMap, splitMap, mergingMap, uniqueValues);

        HashMap<String, List<String>> modifiedState = new HashMap<>();
        HashMap<String, List<String>> modified = modifiedStatus(filesSeen, headMap, splitMap, mergingMap,
                modifiedState);

        mergeLogic(modified, headMap, mergingMap);

        String mergeLogMessage = "Merged " + mergingBranch + " into " + Helper.getActiveBranch() + ".";
        Commit.makeCommit(mergeLogMessage, workingBranchPointer, mergeLogMessage);
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Helper method to list all the unique files in a commit branch. Returned as a set.
     * */
    private static Set<String> filesInCommits(HashMap<String, String> headMap,HashMap<String, String> splitMap,
                                     HashMap<String, String> mergingMap, Set<String> uniqueValues) {
        Collection<String> headFiles = headMap.values();
        Collection<String> splitFiles = splitMap.values();
        Collection<String> mergingFiles = mergingMap.values();

        uniqueValues.addAll(headFiles);
        uniqueValues.addAll(splitFiles);
        uniqueValues.addAll(mergingFiles);
        return uniqueValues;
    }
    /**
     * Helper method to determine the status of a file in relation to the split point version of that file.
     * Returns a hashmap with the file as the key and a list of the modified state for each of the three points of
     * interest.
     * */
    private static HashMap<String, List<String>> modifiedStatus (Set<String> filesSeen, HashMap<String, String> headMap,
                                                HashMap<String, String> splitMap, HashMap<String, String> mergingMap,
                                                                 HashMap<String, List<String>> modifiedState) {
        /*Go through commits as follows: Split, head, merging*/
        /*List to hold the state of the file wrt the commit
        * [splitMap, headMap, mergingMap]
        * */
        /*Variables to hold states of the commits*/
        String splitState = "";
        String headState = "";
        String mergingState = "";
        String splitSHA = "";
        String headSHA = "";
        String mergingSHA = "";

//        go through each of the files that will be seen in the merge
        for (String file : filesSeen) {
             /*ArrayList that will hold the state of the of all three commits in a list for a given file.*/
            List<String> thruple = new ArrayList<>(3);

            /*check if the file is tracked by the commits in question. If so get the associated SHA id.
             * If not tracked in the commit, put null.*/
            if (splitMap.containsValue(file)){
                splitSHA = Helper.getKeyFromValue(splitMap, file);
            } else {
                splitSHA = "null";
            }
            if (headMap.containsValue(file)) {
                headSHA = Helper.getKeyFromValue(headMap, file);
            } else {
                headSHA = "null";
            }
            if (mergingMap.containsValue(file)) {
                mergingSHA = Helper.getKeyFromValue(mergingMap, file);
            } else {
                mergingSHA = "null";
            }
            /*Determine the state of the file wrt to splitSHA
             * IF splitSHA == {headSHA, mergingSHA} then headSHA has not been modified
             * IF splitSHA != {headSHA, mergingSHA} implies that the file in the current head or merging head
             * is different. */

            splitState = splitSHA;
            if (splitSHA.equals("null")) {
                splitState = "null";
            } else {
                splitState = "present";
            }

            if (splitSHA.equals(headSHA)) {
                headState = "unmodified";
            } else if (splitSHA.equals("null") && headSHA.equals("null")) {
                headState = "unmodified";
            } else if (splitSHA.equals("null") && !headSHA.equals("null")) {
                headState = "modified";
            } else {
                headState = "modified";
            }

            if (splitSHA.equals(mergingSHA)) {
                mergingState = "unmodified";
            } else if (splitSHA.equals("null") && mergingSHA.equals("null")) {
                mergingState = "unmodified";
            } else if (splitSHA.equals("null") && !mergingSHA.equals("null")) {
                mergingState = "modified";
            } else {
                mergingState = "modified";
            }

            thruple.add(splitState);
            thruple.add(headState);
            thruple.add(mergingState);

            modifiedState.put(file, thruple);

        }
        return modifiedState;
    }
    /**
     * Method that will handle all the logic of merge. Each of the cases detailed in the spec are here as well as
     * the changes to the working directory.
     * */
    private static void mergeLogic (HashMap<String, List<String>> modifiedState, HashMap<String, String> headMap,
                                    HashMap<String, String> mergingMap) {
        for (Map.Entry<String, List<String>> entry : modifiedState.entrySet()) {
            String filename = entry.getKey();
            List<String> modifiedStatus = entry.getValue();
            // MODIFIED STATUS [SPLIT, HEAD OTHER]
            String split = modifiedStatus.get(0);
            String head = modifiedStatus.get(1);
            String other = modifiedStatus.get(2);

            if (head.equals("unmodified") && other.equals("modified")) {
                if (!mergingMap.containsValue(filename)) {
                    //CASE 6: Unmodified in HEAD but NOT PRESENT in OTHER: REMOVE
                    Repository.rm(filename);
                } else {
                    //CASE 1: Modified in OTHER, Unmodified in HEAD: KEEP OTHER
                    keepFileWithStage(filename, mergingMap);
                }
            } else if (head.equals("modified") && other.equals("unmodified")) {
                continue;
            } else if (head.equals("modified") && other.equals("modified")) {
                //CASE 3: Modified in OTHER and HEAD: {in same way : keep either, in diff ways : conflict}
                //TODO: figure out how to manage this case with the modifications
                checkFileContents(filename, headMap, mergingMap);
            } else if (split.equals("null")&& head.equals("modified") && other.equals("unmodified")) {
                //CASE 4: Not in SPLIT nor OTHER but in HEAD: KEEP HEAD
                continue;
            } else if (split.equals("null")&& head.equals("unmodified") && other.equals("modified")) {
                //CASE 5: Not in SPLIT nor HEAD but in OTHER: KEEP OTHER
                keepFileWithStage(filename, mergingMap);
            } else {
                continue;
            }
            //System.out.println(Utils.readContentsAsString(Utils.join(Repository.CWD, filename)));
        }

    }
    private static void keepFileWithStage (String filename, HashMap<String, String> map) {
        String blobID = Helper.getKeyFromValue(map, filename);
        Helper.readWriteBlobFromCommit(blobID, filename);
        Repository.add(filename);
    }
    private static void keepFileNoStage (String filename, HashMap<String, String> map) {
        String blobID = Helper.getKeyFromValue(map, filename);
        Helper.readWriteBlobFromCommit(blobID, filename);
    }
    /**
     * Method to check the file contents of the file of interest at both the head commit and given branch.
     * This is when both the files are "Modified" in reference to the split point.
     * This is where the logic for conflicts is determined and handled.
     * */
    private static void checkFileContents (String filename, HashMap<String, String> headMap,
                                           HashMap<String, String> mergingMap ) {

        if (!headMap.containsValue(filename) && !mergingMap.containsValue(filename)) {
            //both removed: so leave as is
        } else if (!headMap.containsValue(filename) && mergingMap.containsValue(filename)) {
            //case when file is NOT PRESENT in head commit but PRESENT in merging
            //conflict
            String blobIDMerging = Helper.getKeyFromValue(mergingMap, filename);
            String mergingContent = Helper.readInBlobToString(blobIDMerging);
            String headContent = "";
            handleConflict(filename, headContent, mergingContent);
        } else if (!mergingMap.containsValue(filename) && headMap.containsValue(filename)) {
            //case when file is not present in merging commit but present in head
            //conflict
            String blobIDHead = Helper.getKeyFromValue(headMap, filename);
            String headContent = Helper.readInBlobToString(blobIDHead);
            String mergingContent = "";
            handleConflict(filename, headContent, mergingContent);
        } else if (headMap.containsValue(filename) && mergingMap.containsValue(filename)) {
            String blobIDHead = Helper.getKeyFromValue(headMap, filename);
            String blobIDMerging = Helper.getKeyFromValue(mergingMap, filename);
            byte[] contentHead = Helper.readInBlob(blobIDHead);
            byte[] contentMerging = Helper.readInBlob(blobIDMerging);
            if (Arrays.equals(contentHead, contentMerging)) {
                //same contents: so pick one of the files doesn't matter
//            String blobIDHead = Helper.getKeyFromValue(headMap, filename);
                Helper.readWriteBlobFromCommit(blobIDHead, filename);
                Repository.add(filename);
            }
            else {
                // different contents: Conflict
                String mergingContent = Helper.readInBlobToString(blobIDMerging);
                String headContent = Helper.readInBlobToString(blobIDHead);
                handleConflict(filename, headContent, mergingContent);
            }
        }
    }
    private static void handleConflict(String filename, String headContent, String mergingContent) {
        String combinedContent;
        if (headContent.isEmpty()) {
            combinedContent = "<<<<<<< HEAD\n" + headContent +  "=======\n" + mergingContent + ">>>>>>>";
        } else if (mergingContent.isEmpty()) {
            combinedContent = "<<<<<<< HEAD\n" + headContent + "=======\n" + mergingContent
                    + ">>>>>>>";
        } else {
            combinedContent = "<<<<<<< HEAD\n" + headContent +  "=======\n" + mergingContent
                    + ">>>>>>>";
        }

        writeFile(filename, combinedContent);
        conflict = true;
        System.out.println(combinedContent);
        Repository.add(filename);
    }
    private static void writeFile (String filename, String newContents) {
        File fileOfInterest = Utils.join(Repository.CWD, filename);
        fileOfInterest.delete();
        Utils.writeContents(fileOfInterest, newContents);
    }
    private static void checkFileConflicts (String mergingBranchID) {
        Commit c = Commit.fromFileCommit(mergingBranchID);
        HashMap<String, String> map = c.getBlobMap();
        List<String> filesInWorkingDirectory = Utils.plainFilenamesIn(Repository.CWD);

        assert filesInWorkingDirectory != null;
        for (String file : filesInWorkingDirectory) {
            if (!Helper.fileTrackedByCurrentCommit(file) && Helper.fileTrackedByCommitOnly(file, mergingBranchID)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

}


























