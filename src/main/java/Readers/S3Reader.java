package Readers;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import model.AWSCustomerReview;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class S3Reader implements FileReader {


    @Override
    public List<AWSCustomerReview> getFile(String loc, String fileName, char seperator) {
        try {

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();


            List<Bucket> buckets=s3Client.listBuckets();
            for(Bucket b:buckets){

                System.out.println(b.getName());
            }

            System.out.println(String.format("File Location: %s + File Name: %s",loc,fileName));

            S3Object s3Object = s3Client.getObject(new GetObjectRequest(loc, fileName));
            BufferedReader br = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));


//            ColumnPositionMappingStrategy employeeMapper=new ColumnPositionMappingStrategy();
//            employeeMapper.setType(AWSCustomerReview.class);
//
//            String[] pos=new String[]{"marketplace","customer_id","review_id","product_id","product_parent","product_title","product_category","star_rating","helpful_votes","total_votes","vine","verified_purchase","review_headline","review_body","review_date"};
//            employeeMapper.setColumnMapping(pos);


            CsvToBeanBuilder v = new CsvToBeanBuilder(br).withEscapeChar('\n')
                    .withEscapeChar('\n')
                    .withIgnoreQuotations(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(seperator)
                    .withSkipLines(1);
            List<AWSCustomerReview> results= v.build().parse();



            return results;

        }catch (Exception ex){

            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public List<String[]> getFileAsArray(String loc,String fileName, char seperator) {
        try {

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();


            List<Bucket> buckets=s3Client.listBuckets();
            for(Bucket b:buckets){

                System.out.println(b.getName());
            }

            System.out.println(String.format("File Location: %s + File Name: %s",loc,fileName));

            S3Object s3Object = s3Client.getObject(new GetObjectRequest(loc, fileName));
            BufferedReader br = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));


           CSVParser parser= new CSVParserBuilder()
                    .withSeparator(seperator)
                   .withIgnoreQuotations(true)
                   .withIgnoreLeadingWhiteSpace(true)
                   .build();
            CSVReader reader = new CSVReaderBuilder(br)
                    .withCSVParser(parser)
                    .build();

            return reader.readAll();

        }catch (Exception ex){

            System.out.println(ex.getMessage());
        }
        return null;
    }
}
