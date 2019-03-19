package Writers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalWriter implements FileWriter{


    @Override
    public boolean writeFile(String loc, String filename, List<String> newFile) {

        try {
           Path writtenFile= Files.write(Paths.get(loc,filename),newFile);
           return writtenFile.toFile().exists();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
