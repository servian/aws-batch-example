package model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString(includeFieldNames = true)
@Data public class AWSCustomerReview implements Serializable  {

    String marketplace;
    String customer_id;
    String review_id;
    String product_id;
    String product_parent;
    String product_title;
    String product_category;
    String star_rating;
    String helpful_votes;
    String total_votes;
    String vine;
    String verified_purchase;
    String review_headline;
    String review_body;
    String review_date;

}
