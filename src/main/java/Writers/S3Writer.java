package Writers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.Md5Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class S3Writer implements FileWriter {
    @Override
    public boolean writeFile(String loc, String filename, List<String> newFile) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();

            Path writtenFile = Files.write(Paths.get("./", "batchTemp"), newFile);

            File tempfile = writtenFile.toFile();
            byte[] hash = Md5Utils.computeMD5Hash(tempfile);

            String ogHash = new String(hash, StandardCharsets.UTF_8);

            PutObjectResult result = s3Client
                    .putObject(new PutObjectRequest(loc, filename, writtenFile.toFile()));

            boolean match = ogHash.equals(result.getContentMd5());
            tempfile.delete();

            return match;


        } catch (Exception ex) {

            System.out.println(ex.getMessage());

        }
        return false;
    }
}
