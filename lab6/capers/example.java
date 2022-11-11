package capers;
import java.io.File;
import java.io.IOException;


public class example {
    public static void main(String[] args) throws IOException {
        File exampleFile = new File("example.txt");
        exampleFile.createNewFile();
        File exampleDirectory = new File(".exampleDir");
        exampleDirectory.mkdir();
        File exampleDirSub = Utils.join(".exampleDir", "subDir");
        exampleDirSub.mkdir();
    }
}
