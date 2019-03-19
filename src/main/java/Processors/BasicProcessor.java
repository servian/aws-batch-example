package Processors;

import Readers.FileReader;
import Readers.S3Reader;
import Writers.FileWriter;
import Writers.S3Writer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentItemResult;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentRequest;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentResult;
import com.amazonaws.services.comprehend.model.LanguageCode;
import model.CustomerResult;


import java.util.*;

public class BasicProcessor {

    public  static String DEST_FOLDER="sentimentScore";

    /**
     * A Simple Method that read a "|" seperated file from AWS_BATCH_IP_SOURCE_BUCKET
     * and prints the third column in an s3 file stored in AWS_BATCH_IP_DESTINATION_BUCKET.
     * @param parameters: Command Line Args based to Job
     *                  AWS_BATCH_IP_SOURCE_BUCKET : AWS S3 source bucket name
     *                  AWS_BATCH_IP_SOURCE_BUCKET_KEY : AWS S3 object key
     *                  AWS_BATCH_IP_DESTINATION_BUCKET: AWS S3 destination bucket name
     *                  AWS_BATCH_IP_DESTINATION_KEY:  AWS S3 destination object key
     */
    public static void process(HashMap<String,String> parameters) {

        String source = parameters.get("AWS_BATCH_IP_SOURCE_BUCKET");
                System.out.println("source " + source);
        String key = parameters.get("AWS_BATCH_IP_SOURCE_BUCKET_KEY");
        System.out.println("key " + key);
        String destination = parameters.get("AWS_BATCH_IP_DESTINATION_BUCKET");
        String destinationKey = parameters.get("AWS_BATCH_IP_DESTINATION_KEY");

        System.out.println("destination " + destination + "/" + destinationKey);
        FileReader reader = new S3Reader();
        List<String[]> data = reader.getFileAsArray(source, key,'|');
        List<String> newFile = new ArrayList<>();

        for (String[] s : data) {
            System.out.println(s[3]);
            newFile.add(s[3]);
        }

        FileWriter c = new S3Writer();
        System.out.println(c.writeFile(destination, destinationKey, newFile));

    }

     /** A Simple Method that read a tab seperated file from AWS s3 source bucket i.e. AWS_BATCH_IP_SOURCE_BUCKET
     * and gets the sentiment score of each line in the file using AWS Comprehend
      * and outputs the scores to AWS s3 destination bucket i.e. AWS_BATCH_IP_DESTINATION_BUCKET
      * @param parameters: Command Line Args based to Job
     *                  AWS_BATCH_IP_SOURCE_BUCKET : AWS S3 source bucket name
     *                  AWS_BATCH_IP_SOURCE_BUCKET_KEY : AWS S3 object key
     *                  AWS_BATCH_IP_DESTINATION_BUCKET: AWS S3 destination bucket name
     *                  AWS_BATCH_IP_DESTINATION_KEY:  AWS S3 destination object key
     */
    public static void processSentiment(HashMap<String,String> parameters) {

        String source = parameters.get("AWS_BATCH_IP_SOURCE_BUCKET");
        System.out.println("source " + source);
        String key = parameters.get("AWS_BATCH_IP_SOURCE_BUCKET_KEY");
        System.out.println("key " + key);
        String destination = parameters.get("AWS_BATCH_IP_DESTINATION_BUCKET");
        String destinationKey = parameters.get("AWS_BATCH_IP_DESTINATION_KEY");

        System.out.println("destination " + destination + "/" + DEST_FOLDER+"/"+ destinationKey);

        FileReader reader = new S3Reader();
        List<String[]> data = reader.getFileAsArray(source, key,',');
        List<String> newFile = new ArrayList<>();
        List<CustomerResult> agg=new ArrayList<>();


        for (String[] s : data) {
           // System.out.println(s[0] +" "+s[1]);
            newFile.add(s[1]);
            agg.add(CustomerResult.builder().
                    reviewId(s[0]).
                    build());
        }


        //region AWS Comprehend setup and send request
        AmazonComprehend sent = AmazonComprehendClientBuilder
                .standard()
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();


        BatchDetectSentimentRequest request= new BatchDetectSentimentRequest()
                .withTextList(newFile)
                .withLanguageCode(LanguageCode.En);

        BatchDetectSentimentResult result= sent.
                batchDetectSentiment(request);
        //endregion

        Iterator<CustomerResult> iter= agg.iterator();
        CustomerResult temp;
        List<String> rOut=new ArrayList<>();

        for(BatchDetectSentimentItemResult x: result.getResultList()){
            if(iter.hasNext()) {
                temp = iter.next();
                temp.setSentimentResult(x.getSentiment()+"("+x.getSentimentScore()+")");
                rOut.add(temp.toString());

            }

        }

        FileWriter c = new S3Writer();
        System.out.println("JOB completed successfully : "+c.writeFile(destination, DEST_FOLDER+"/"+destinationKey, rOut));

    }


    public static void main(String[] args) {

        HashMap<String,String> params= argsToMap(args);
        BasicProcessor.processSentiment(params);
    }


    public static HashMap<String, String> argsToMap(String[] args) {

         //region Parsing cmd args without fancy tools (yeah ! not that great I know!)
        HashMap<String,String> parameters=new HashMap<String, String>();

        for (int i = 0; i <=args.length-2; i=i+2) {

            parameters.put(args[i].trim(),args[i+1].trim());

        }
        //endregion

        return parameters;


    }

}
