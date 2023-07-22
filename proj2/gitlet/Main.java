package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
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
            case "readAddstage":
                //java gitlet.Main commit [message]
                Repository.readAddStage();
                break;
            case "readBlobstracked":
                //java gitlet.Main commit [message]
                String commitID = args[1];
                Commit.readBlobsTracked(commitID);
                break;
            case "readRemovestage":
                //java gitlet.Main commit [message]
                Repository.readRemoveStage();
                break;
            // TODO: FILL THE REST IN
        }
    }
}
