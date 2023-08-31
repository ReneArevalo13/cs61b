package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author RENE AREVALO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args)  {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command. ");
        }
        switch (args[0]) {
            case "init":
                Repository.init();
                break;
            case "add":
                String fileArg = args[1];
                Repository.add(fileArg);
                break;
            case "commit":
                //java gitlet.Main commit [message]
                String commitMessage = args[1];
                Commit.makeCommit(commitMessage);
                break;
            case "rm":
                fileArg = args[1];
                Repository.rm(fileArg);
                break;
            case "log":
                Commit.log();
                break;
            case "global-log":
                Commit.globalLog();
                break;
            case "find":
                String findMessage = args[1];
                Commit.find(findMessage);
                break;
            case "checkout":
                if (args.length != 2 && args.length != 3 && args.length != 4) {
                    System.out.println("Incorrect Operands");
                } else if ((args.length == 4 && !args[2].equals("--"))
                        || (args.length == 3 && !args[1].equals("--"))) {
                    System.out.println("Incorrect Operands");
                } else {
                    Repository.checkout(args);
                }
                break;
            case "branch":
                String branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch":
                String rmBranchName = args[1];
                Repository.removeBranch(rmBranchName);
                break;
            case "status":
                Repository.status();
                break;
            case "reset":
                String resetID = args[1];
                Repository.reset(resetID);
                break;
            case "merge":
                String mergingBranch = args[1];
                Merge.merge(mergingBranch);
                break;
            case "readAddstage":
                //java gitlet.Main commit [message]
                Repository.readAddStage();
                break;
            case "readBlobstracked":
                //java gitlet.Main commit [message]
                String commitID = args[1];
                Commit.readBlobsTracked(commitID);
                break;

            case "splitPoint":
                String otherBranch = args[1];
                Merge.splitPoint(otherBranch);
                break;
            // TODO: FILL THE REST IN
        }
    }
}
