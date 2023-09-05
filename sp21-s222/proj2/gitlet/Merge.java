package gitlet;


import javax.swing.text.Style;
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
        System.out.println(filesSeen);
        HashMap<String, List<String>> modifiedState = new HashMap<>();
        HashMap<String, List<String>> modified = modifiedStatus(filesSeen, headMap, splitMap, mergingMap, modifiedState);

        for (HashMap.Entry<String, List<String>> entry : modified.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }

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
}


























