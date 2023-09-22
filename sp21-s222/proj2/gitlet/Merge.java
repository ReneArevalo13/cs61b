package gitlet;


import java.io.File;
import java.util.*;


public class Merge {
    public static Set<String>  filesSeen;
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
        System.out.println("This is the split point id: " + minkey);
        return minkey;
    }

    /**
     * Recursive method to iterate through the commits following the parent and get a total mapping of a "branch".
     * */
    private static HashMap<String, Integer> goThroughCommits(String commitPointer, HashMap<String, Integer> commitMap,
                                                             Integer counter) {
        Commit c = Commit.fromFileCommit(commitPointer);
        if (c.getParent().equals("null")) {
            counter++;
            commitMap.put(c.getId(), counter);
            return commitMap;
        } else {
            commitMap.put(c.getId(), counter);
            counter++;
            goThroughCommits(c.getParent(), commitMap, counter);
        }
        return commitMap;
    }
    /**
     * Driver method to execute the Merge command from spec.*/
    public static void merge(String mergingBranch) {
        File currentHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", Helper.getActiveBranch());
        File mergingHead = Utils.join(Repository.CWD, ".gitlet", "refs", "head", mergingBranch);

        String workingBranchPointer = Utils.readContentsAsString(currentHead);
        String mergingBranchPointer = Utils.readContentsAsString(mergingHead);
        String splitPointID = splitPoint(mergingBranch);

        Commit splitPointCommit = Commit.fromFileCommit(splitPointID);
        Commit headCommit = Commit.fromFileCommit(workingBranchPointer);
        Commit mergingCommit = Commit.fromFileCommit(mergingBranchPointer);

        HashMap<String, String> headMap = headCommit.getBlobMap();
        HashMap<String, String> splitMap = splitPointCommit.getBlobMap();
        HashMap<String, String> mergingMap = mergingCommit.getBlobMap();

        Set<String> uniqueValues = new HashSet<>();
        Set<String>  filesSeen = filesInCommits(headMap, splitMap, mergingMap, uniqueValues);

        HashMap<String, List<String>> modifiedState = new HashMap<>();
        HashMap<String, List<String>> modified = modifiedStatus(filesSeen, headMap, splitMap, mergingMap, modifiedState);


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

            if (headSHA.equals("null")) {
                headState = "null";
            } else if (splitSHA.equals(headSHA)) {
                headState = "unmodified";
            } else if (splitSHA.equals("null") && !headSHA.equals("null")) {
                headState = "present";
            } else {
                headState = "modified";
            }

            if (mergingSHA.equals("null")) {
                mergingState = "null";
            } else if (splitSHA.equals(mergingSHA)) {
                mergingState = "unmodified";
            } else if (splitSHA.equals("null") && !mergingSHA.equals("null")) {
                mergingState = "present";
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
                //CASE 1: Modified in OTHER, Unmodified in HEAD: KEEP OTHER
                keepFileWithStage(filename, mergingMap);
            } else if (head.equals("modified") && other.equals("unmodified")) {
                //CASE 2: Modified in HEAD, Unmodified in OTHER: KEEP HEAD
                continue;
            } else if (head.equals("modified") && other.equals("modified")) {
                //CASE 3: Modified in OTHER and HEAD: {in same way : keep either, in diff ways : conflict}
                //TODO: figure out how to manage this case with the modifications
            } else if (split.equals("null")&& head.equals("present") && other.equals("null")) {
                //CASE 4: Not in SPLIT nor OTHER but in HEAD: KEEP HEAD
                continue;
            } else if (split.equals("null")&& head.equals("null") && other.equals("present")) {
                //CASE 5: Not in SPLIT nor HEAD but in OTHER: KEEP OTHER
                keepFileWithStage(filename, mergingMap);
            } else if (head.equals("unmodified") && other.equals("null")) {
                //CASE 6: Unmodified in HEAD but NOT PRESENT in OTHER: REMOVE
                Repository.rm(filename);
            } else if (head.equals("null") && other.equals("unmodified")) {
                //CASE 7: Unmodified in OTHER but NOT PRESENT in HEAD: REMOVE
                continue;
            } else if (split.equals("null")&& head.equals("present") && other.equals("present")) {
                //last case with conflict: not at split but present at head and merging
            } else {
                continue;
            }
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
    private static void checkFileContents (String filename, HashMap<String, String> headMap,
                                           HashMap<String, String> meringMap ) {

        String blobIDHead = Helper.getKeyFromValue(headMap, filename);
        String blobIDMerging = Helper.getKeyFromValue(meringMap, filename);

        byte[] contentHead = Helper.readInBlob(blobIDHead, filename);
        byte[] contentMerging = Helper.readInBlob(blobIDMerging, filename);

        if (!headMap.containsValue(filename) && !meringMap.containsValue(filename)) {
            //both removed: so leave as is
        } else if (!headMap.containsValue(filename) && meringMap.containsValue(filename)) {
            //case when file is not present in head commit but present in merging
            //conflict
            handleConflict(filename, blobIDHead, blobIDMerging);
        } else if (!meringMap.containsValue(filename) && headMap.containsValue(filename)) {
            //case when file is not present in merging commit but present in head
            //conflict
            handleConflict(filename, blobIDHead, blobIDMerging);
        }

        if (Arrays.equals(contentHead, contentMerging)) {
            //same contents: so pick one of the files doesn't matter
            Helper.readWriteBlobFromCommit(blobIDHead, filename);
            Repository.add(filename);
        } else {
            // different contents: Conflict
            handleConflict(filename, blobIDHead, blobIDMerging);

        }

    }
    private static void handleConflict(String filename, String blobIDHead, String blobIDMerging) {
        String headContents = Helper.readInBlobToString(blobIDHead, filename);
        String mergingContents = Helper.readInBlobToString(blobIDMerging, filename);
        String combinedContents = "<<<<<<< HEAD\n" + headContents + "\n" + "=======\n" + mergingContents + "\n"
                + ">>>>>>>";
        writeFile(filename, combinedContents);
        Repository.add(filename);
        System.out.println(combinedContents);

    }
    private static void writeFile (String filename, String newContents) {
        File fileOfInterest = Utils.join(Repository.CWD, filename);
        Utils.writeContents(fileOfInterest, newContents);
    }


}


























