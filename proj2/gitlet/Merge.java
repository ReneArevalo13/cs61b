package gitlet;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.join;

public class Merge {
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

    private static HashMap<String, Integer> goThroughCommits(String commitPointer, HashMap<String, Integer> commitMap, Integer counter) {
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
}
