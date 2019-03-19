package Readers;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import model.AWSCustomerReview;

import java.util.List;

public class LocalReader implements FileReader {



    @Override
    public List<AWSCustomerReview> getFile(String loc, String fileName, char sep) {
        try {
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            //CSVReader reader = new CSVReader(new java.io.FileReader(loc+"/"+fileName),'\t');
            ColumnPositionMappingStrategy employeeMapper=new ColumnPositionMappingStrategy();
            employeeMapper.setType(AWSCustomerReview.class);

            String[] pos=new String[]{"marketplace","customer_id","review_id","product_id","product_parent","product_title","product_category","star_rating","helpful_votes","total_votes","vine","verified_purchase","review_headline","review_body","review_date"};
            employeeMapper.setColumnMapping(pos);

            java.io.FileReader vv= new java.io.FileReader(loc+"/"+fileName);

            CsvToBeanBuilder v = new CsvToBeanBuilder(new java.io.FileReader(loc+"/"+fileName))
                    .withMappingStrategy(employeeMapper)
                    .withEscapeChar('\n')
                    .withIgnoreQuotations(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(sep)
                    .withSkipLines(1);
            // List<AWSCustomerReview> result = new CsvToBeanBuilder(vv).withType(AWSCustomerReview.class).withSeparator('\t').build().parse();
            List<AWSCustomerReview> results= v.build().parse();




            return results;

        }catch (Exception ex){

        }
        return null;
    }

    @Override
    public List<String[]> getFileAsArray(String loc,String fileName,char sep) {
        try {
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            CSVReader reader = new CSVReader(new java.io.FileReader(loc+"/"+fileName),sep);
            return reader.readAll();

        }catch (Exception ex){

        }
        return null;
    }
}
