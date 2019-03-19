package Readers;


import model.AWSCustomerReview;

import java.util.List;

public interface FileReader {

    public List<String[]> getFileAsArray(String loc,String filename,char seperator);
    List<AWSCustomerReview> getFile(String loc, String filename, char seperator);



}
